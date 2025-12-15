package com.ads.loadoutsmanager.data.api

import com.ads.loadoutsmanager.data.auth.SecureTokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import net.openid.appauth.AuthorizationService

/**
 * OkHttp Authenticator for automatic token refresh
 * 
 * Intercepts 401 Unauthorized responses and attempts to refresh the access token
 */
class TokenRefreshAuthenticator(
    private val tokenStorage: SecureTokenStorage,
    private val oauth2Manager: OAuth2Manager,
    private val authorizationService: AuthorizationService
) : Authenticator {
    
    override fun authenticate(route: Route?, response: Response): Request? {
        // If already tried to refresh, don't try again
        if (response.request.header("Authorization") != response.request.header("X-Retried-Authorization")) {
            return null
        }
        
        // Get refresh token
        val refreshToken = tokenStorage.getRefreshToken() ?: return null
        
        // Try to refresh synchronously (we're already on background thread)
        var newAccessToken: String? = null
        
        runBlocking {
            oauth2Manager.refreshAccessToken(
                authorizationService = authorizationService,
                refreshToken = refreshToken
            ) { accessToken, exception ->
                if (accessToken != null) {
                    newAccessToken = accessToken
                } else {
                    // Refresh failed, user needs to re-authenticate
                    tokenStorage.clearTokens()
                }
            }
        }
        
        // If we got a new token, retry the request with it
        return newAccessToken?.let { token ->
            response.request.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("X-Retried-Authorization", "Bearer $token")
                .build()
        }
    }
}
