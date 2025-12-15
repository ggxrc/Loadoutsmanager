package com.ads.loadoutsmanager.data.api

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.ads.loadoutsmanager.data.auth.SecureTokenStorage
import net.openid.appauth.*

/**
 * Manages OAuth2 authentication flow with Bungie.net
 * 
 * OAuth2 Configuration:
 * - Authorization Endpoint: https://www.bungie.net/en/OAuth/Authorize
 * - Token Endpoint: https://www.bungie.net/platform/app/oauth/token/
 * - Client ID: Must be registered at https://www.bungie.net/en/Application
 */
class OAuth2Manager(
    private val context: Context,
    private val clientId: String
) {
    
    private val tokenStorage = SecureTokenStorage(context)
    
    companion object {
        private const val AUTHORIZATION_ENDPOINT = "https://www.bungie.net/en/OAuth/Authorize"
        private const val TOKEN_ENDPOINT = "https://www.bungie.net/platform/app/oauth/token/"
        private const val REDIRECT_URI = "com.ads.loadoutsmanager://oauth2redirect"
        
        // Bungie API scopes
        const val SCOPE_READ_GROUPS = "ReadGroups"
        const val SCOPE_WRITE_GROUPS = "WriteGroups"
        const val SCOPE_ADMIN_GROUPS = "AdminGroups"
        const val SCOPE_BUNGIE_NET = "BungieNext"
        const val SCOPE_MOVE_EQUIP_DESTINY_ITEMS = "MoveEquipDestinyItems"
        const val SCOPE_READ_DESTINY_INVENTORY_AND_VAULT = "ReadDestinyInventoryAndVault"
        const val SCOPE_READ_USER_DATA = "ReadUserData"
        const val SCOPE_EDIT_USER_DATA = "EditUserData"
        const val SCOPE_READ_DESTINY_VENDORS_AND_ADVISORS = "ReadDestinyVendorsAndAdvisors"
        const val SCOPE_READ_AND_APPLY_TOKENS_AND_CODES = "ReadAndApplyTokensAndCodes"
        const val SCOPE_ADVANCED_WRITE_ACTIONS = "AdvancedWriteActions"
        const val SCOPE_PARTNER_OFFER_GRANT = "PartnerOfferGrant"
        const val SCOPE_DESTINY_UNLOCK_VALUE_QUERY = "DestinyUnlockValueQuery"
        const val SCOPE_USER_PINNED_ITEMS_READ = "UserPinnedItemsRead"
        const val SCOPE_USER_PINNED_ITEMS_UPDATE = "UserPinnedItemsUpdate"
        const val SCOPE_READ_DESTINY_METRICS = "ReadDestinyMetrics"
    }
    
    private val serviceConfig = AuthorizationServiceConfiguration(
        Uri.parse(AUTHORIZATION_ENDPOINT),
        Uri.parse(TOKEN_ENDPOINT)
    )
    
    /**
     * Creates an authorization request for loadout management
     * Includes necessary scopes for reading and writing Destiny items
     */
    fun createAuthorizationRequest(): AuthorizationRequest {
        return AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            Uri.parse(REDIRECT_URI)
        ).setScopes(
            SCOPE_READ_USER_DATA,
            SCOPE_READ_DESTINY_INVENTORY_AND_VAULT,
            SCOPE_MOVE_EQUIP_DESTINY_ITEMS
        ).build()
    }
    
    /**
     * Performs the authorization request
     */
    fun performAuthorizationRequest(
        authorizationService: AuthorizationService,
        authRequest: AuthorizationRequest,
        authIntent: Intent
    ) {
        authorizationService.performAuthorizationRequest(
            authRequest,
            PendingIntent.getActivity(
                context,
                0,
                authIntent,
                PendingIntent.FLAG_MUTABLE
            )
        )
    }
    
    /**
     * Exchanges authorization code for access token
     */
    fun exchangeAuthorizationCode(
        authorizationService: AuthorizationService,
        authResponse: AuthorizationResponse,
        callback: (String?, AuthorizationException?) -> Unit
    ) {
        authorizationService.performTokenRequest(
            authResponse.createTokenExchangeRequest()
        ) { tokenResponse, exception ->
            if (tokenResponse != null && tokenResponse.accessToken != null) {
                // Save tokens securely
                tokenResponse.refreshToken?.let { refreshToken ->
                    tokenStorage.saveTokens(
                        accessToken = tokenResponse.accessToken!!,
                        refreshToken = refreshToken,
                        expiresIn = tokenResponse.accessTokenExpirationTime?.let {
                            (it - System.currentTimeMillis()) / 1000
                        } ?: 3600 // Default 1 hour
                    )
                }
                callback(tokenResponse.accessToken, null)
            } else {
                callback(null, exception)
            }
        }
    }
    
    /**
     * Refreshes the access token using refresh token
     */
    fun refreshAccessToken(
        authorizationService: AuthorizationService,
        refreshToken: String,
        callback: (String?, AuthorizationException?) -> Unit
    ) {
        val tokenRequest = TokenRequest.Builder(
            serviceConfig,
            clientId
        )
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setRefreshToken(refreshToken)
            .build()
        
        authorizationService.performTokenRequest(tokenRequest) { tokenResponse, exception ->
            if (tokenResponse != null && tokenResponse.accessToken != null) {
                // Update stored tokens
                tokenResponse.refreshToken?.let { newRefreshToken ->
                    tokenStorage.saveTokens(
                        accessToken = tokenResponse.accessToken!!,
                        refreshToken = newRefreshToken,
                        expiresIn = tokenResponse.accessTokenExpirationTime?.let {
                            (it - System.currentTimeMillis()) / 1000
                        } ?: 3600
                    )
                }
                callback(tokenResponse.accessToken, null)
            } else {
                callback(null, exception)
            }
        }
    }
    
    /**
     * Get current access token from storage
     */
    fun getAccessToken(): String? {
        return tokenStorage.getAccessToken()
    }
    
    /**
     * Check if token is expired
     */
    fun isTokenExpired(): Boolean {
        return tokenStorage.isTokenExpired()
    }
    
    /**
     * Clear all tokens (logout)
     */
    fun logout() {
        tokenStorage.clearTokens()
    }
}
