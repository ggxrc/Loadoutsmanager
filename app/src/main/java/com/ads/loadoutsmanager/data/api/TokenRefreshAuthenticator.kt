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
        android.util.Log.d("TokenRefreshAuthenticator", "🔄 Authenticator called for ${response.request.url}")
        
        // If already tried to refresh, don't try again
        val authHeader = response.request.header("Authorization")
        val retryHeader = response.request.header("X-Retried-Authorization")
        
        android.util.Log.d("TokenRefreshAuthenticator", "Headers - Auth: ${authHeader?.take(20)}..., Retry: ${retryHeader?.take(20)}...")
        
        if (authHeader == retryHeader) {
            android.util.Log.w("TokenRefreshAuthenticator", "🚫 Already tried refresh, giving up")
            return null
        }
        
        // Get refresh token
        val refreshToken = tokenStorage.getRefreshToken()
        if (refreshToken == null) {
            android.util.Log.e("TokenRefreshAuthenticator", "❌ No refresh token available")
            return null
        }
        
        android.util.Log.d("TokenRefreshAuthenticator", "🔄 Attempting token refresh...")
        
        // Try to refresh synchronously (we're already on background thread)
        var newAccessToken: String? = null
        
        runBlocking {
            oauth2Manager.refreshAccessToken(
                authorizationService = authorizationService,
                refreshToken = refreshToken
            ) { accessToken, exception ->
                if (accessToken != null) {
                    newAccessToken = accessToken
                    android.util.Log.d("TokenRefreshAuthenticator", "✅ Token refresh successful")
                } else {
                    android.util.Log.e("TokenRefreshAuthenticator", "❌ Token refresh failed: ${exception?.message}")
                    // Refresh failed, user needs to re-authenticate
                    tokenStorage.clearTokens()
                }
            }
        }
        
        // If we got a new token, retry the request with it
        return newAccessToken?.let { token ->
            android.util.Log.d("TokenRefreshAuthenticator", "🔄 Retrying request with new token")
            response.request.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("X-Retried-Authorization", "Bearer $token")
                .build()
        } ?: run {
            android.util.Log.w("TokenRefreshAuthenticator", "⚠️ No new token, cannot retry")
            null
        }
    }
}
