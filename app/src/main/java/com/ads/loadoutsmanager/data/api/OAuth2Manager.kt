package com.ads.loadoutsmanager.data.api

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.ads.loadoutsmanager.data.auth.SecureTokenStorage
import net.openid.appauth.*

/**
 * Manages OAuth2 authentication flow with Bungie.net
 * Uses credentials from BungieConfig (loaded from local.properties)
 */
class OAuth2Manager(
    private val context: Context,
    private val tokenStorage: SecureTokenStorage
) {

    private val serviceConfig = AuthorizationServiceConfiguration(
        Uri.parse(BungieConfig.AUTHORIZATION_ENDPOINT),
        Uri.parse(BungieConfig.TOKEN_ENDPOINT)
    )
    
    /**
     * Creates an authorization request for loadout management
     * Bungie API does not support specifying scopes in the authorization request.
     * Scopes are defined in the Application settings on Bungie.net portal.
     */
    fun createAuthorizationRequest(): AuthorizationRequest {
        return AuthorizationRequest.Builder(
            serviceConfig,
            BungieConfig.clientId,
            ResponseTypeValues.CODE,
            Uri.parse(BungieConfig.REDIRECT_URI)
        )
        // Scopes must not be sent for Bungie.net
        // .setScopes(...) 
        .build()
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
        val clientAuthentication = ClientSecretPost(BungieConfig.clientSecret)

        authorizationService.performTokenRequest(
            authResponse.createTokenExchangeRequest(),
            clientAuthentication
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
            BungieConfig.clientId
        )
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setRefreshToken(refreshToken)
            .build()
        
        val clientAuthentication = ClientSecretPost(BungieConfig.clientSecret)

        authorizationService.performTokenRequest(tokenRequest, clientAuthentication) { tokenResponse, exception ->
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
     * Clear all tokens (logout)
     */
    fun logout() {
        tokenStorage.clearTokens()
    }
}