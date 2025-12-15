package com.ads.loadoutsmanager.presentation.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ads.loadoutsmanager.BuildConfig
import com.ads.loadoutsmanager.MainActivity
import com.ads.loadoutsmanager.data.api.OAuth2Manager
import com.ads.loadoutsmanager.data.auth.SecureTokenStorage
import com.ads.loadoutsmanager.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService

/**
 * ViewModel for managing authentication flow
 */
class AuthViewModel(
    private val tokenStorage: SecureTokenStorage,
    private val authRepository: AuthRepository,
    private val context: Context
) : ViewModel() {

    private val oauth2Manager = OAuth2Manager(context, tokenStorage)

    private val _authState = MutableStateFlow<AuthState>(AuthState.NotAuthenticated)
    val authState: StateFlow<AuthState> = _authState

    sealed class AuthState {
        object NotAuthenticated : AuthState()
        object Authenticating : AuthState()
        data class Authenticated(val displayName: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    init {
        checkAuthStatus()
    }

    /**
     * Check if user is already authenticated
     */
    private fun checkAuthStatus() {
        if (tokenStorage.isAuthenticated() && !tokenStorage.isTokenExpired()) {
            viewModelScope.launch {
                authRepository.getCurrentMembership()?.let { membership ->
                    _authState.value = AuthState.Authenticated(membership.displayName)
                } ?: run {
                    _authState.value = AuthState.NotAuthenticated
                }
            }
        }
    }

    /**
     * Start OAuth2 authentication flow
     */
    fun startAuth(authorizationService: AuthorizationService) {
        _authState.value = AuthState.Authenticating

        val authRequest = oauth2Manager.createAuthorizationRequest()

        // Define the intent to be called when the auth flow is complete
        val completionIntent = Intent(context, MainActivity::class.java)
        completionIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        oauth2Manager.performAuthorizationRequest(
            authorizationService,
            authRequest,
            completionIntent
        )
    }

    /**
     * Handle OAuth2 callback
     */
    fun handleAuthCallback(
        authorizationService: AuthorizationService,
        authResponse: AuthorizationResponse?,
        authException: AuthorizationException?
    ) {
        android.util.Log.d("AuthViewModel", "=== handleAuthCallback ===")
        android.util.Log.d("AuthViewModel", "Response: ${authResponse != null}, Exception: ${authException?.message}")

        viewModelScope.launch {
            if (authResponse != null) {
                android.util.Log.d("AuthViewModel", "Starting token exchange...")

                // Exchange authorization code for tokens
                oauth2Manager.exchangeAuthorizationCode(
                    authorizationService = authorizationService,
                    authResponse = authResponse
                ) { accessToken, exception ->
                    android.util.Log.d("AuthViewModel", "Token exchange result: ${accessToken != null}")

                    if (accessToken != null) {
                        android.util.Log.d("AuthViewModel", "Access token received: ${accessToken.take(30)}...")

                        viewModelScope.launch {
                            android.util.Log.d("AuthViewModel", "Calling resolveCrossSaveMembership...")

                            // After getting the access token, resolve the user's Destiny membership
                            val result = authRepository.resolveCrossSaveMembership()

                            result.fold(
                                onSuccess = { membership ->
                                    android.util.Log.d("AuthViewModel", "✅ Auth SUCCESS: ${membership.displayName}")
                                    _authState.value = AuthState.Authenticated(
                                        membership.displayName
                                    )
                                },
                                onFailure = { error ->
                                    android.util.Log.e("AuthViewModel", "❌ Auth FAILED: ${error.message}", error)
                                    _authState.value = AuthState.Error(
                                        error.message ?: "Failed to verify account"
                                    )
                                }
                            )
                        }
                    } else {
                        val errorMsg = exception?.errorDescription ?: exception?.message
                        ?: "Failed to get access token"
                        android.util.Log.e("AuthViewModel", "Token exchange failed: $errorMsg")
                        _authState.value = AuthState.Error(errorMsg)
                    }
                }
            } else {
                val errorMsg = authException?.message ?: "Authentication failed"
                android.util.Log.e("AuthViewModel", "OAuth callback failed: $errorMsg")
                _authState.value = AuthState.Error(errorMsg)
            }
        }
    }

    /**
     * Refresh expired token
     */
    fun refreshToken(authorizationService: AuthorizationService) {
        val refreshToken = tokenStorage.getRefreshToken()

        if (refreshToken != null) {
            oauth2Manager.refreshAccessToken(
                authorizationService = authorizationService,
                refreshToken = refreshToken
            ) { accessToken, exception ->
                if (accessToken != null) {
                    viewModelScope.launch {
                        authRepository.getCurrentMembership()?.let { membership ->
                            _authState.value = AuthState.Authenticated(membership.displayName)
                        }
                    }
                } else {
                    val errorMsg = exception?.errorDescription ?: exception?.message
                    ?: "Failed to refresh token"
                    _authState.value = AuthState.Error(errorMsg)
                }
            }
        } else {
            _authState.value = AuthState.NotAuthenticated
        }
    }

    /**
     * Logout user
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
        oauth2Manager.logout()
        _authState.value = AuthState.NotAuthenticated
    }

    companion object {
        fun Factory(
            tokenStorage: SecureTokenStorage,
            authRepository: AuthRepository,
            context: Context
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                    return AuthViewModel(tokenStorage, authRepository, context) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}