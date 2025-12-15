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
    
    private val oauth2Manager = OAuth2Manager(context, BuildConfig.BUNGIE_CLIENT_ID)
    
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
        viewModelScope.launch {
            if (authResponse != null) {
                // Exchange authorization code for tokens
                oauth2Manager.exchangeAuthorizationCode(
                    authorizationService = authorizationService,
                    authResponse = authResponse
                ) { accessToken, exception ->
                    if (accessToken != null) {
                        viewModelScope.launch {
                            // TODO: Get initial membership info from OAuth response
                            // This part likely needs the user's membership ID from the token response 
                            // or a subsequent call to GetCurrentUser. 
                            // For now we attempt the flow as structured.
                            val membershipType = 3 // Steam as default
                            val membershipId = "temp_id" // Get from OAuth
                            
                            val result = authRepository.resolveCrossSaveMembership(
                                membershipType, 
                                membershipId
                            )
                            
                            result.fold(
                                onSuccess = { membership ->
                                    _authState.value = AuthState.Authenticated(
                                        membership.displayName
                                    )
                                },
                                onFailure = { error ->
                                    _authState.value = AuthState.Error(
                                        error.message ?: "Failed to verify account"
                                    )
                                }
                            )
                        }
                    } else {
                        _authState.value = AuthState.Error(
                            exception?.message ?: "Failed to get access token"
                        )
                    }
                }
            } else {
                _authState.value = AuthState.Error(
                    authException?.message ?: "Authentication failed"
                )
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
                    _authState.value = AuthState.Error(
                        exception?.message ?: "Failed to refresh token"
                    )
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
    
    /**
     * Factory for creating AuthViewModel with dependencies
     */
    class Factory(
        private val tokenStorage: SecureTokenStorage,
        private val authRepository: AuthRepository,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(tokenStorage, authRepository, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}