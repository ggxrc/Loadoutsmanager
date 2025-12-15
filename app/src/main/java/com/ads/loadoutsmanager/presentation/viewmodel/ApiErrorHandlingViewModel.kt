package com.ads.loadoutsmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ads.loadoutsmanager.data.api.ApiResult
import com.ads.loadoutsmanager.data.api.executeApiCall
import com.ads.loadoutsmanager.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel demonstrating proper API error handling
 * Includes handling for:
 * - System maintenance (ErrorCode 5)
 * - Throttling
 * - Generic errors
 */
class ApiErrorHandlingViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState
    
    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()
        data class SystemMaintenance(val message: String, val retryAfterSeconds: Int = 0) : UiState()
        data class Throttled(val retryAfterSeconds: Int) : UiState()
    }
    
    /**
     * Example: Verify Cross-Save with proper error handling
     */
    fun verifyCrossSave(membershipType: Int, membershipId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            val result = authRepository.resolveCrossSaveMembership(membershipType, membershipId)
            result.fold(
                onSuccess = { membership ->
                    _uiState.value = UiState.Success("Cross-Save verified for ${membership.displayName}")
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
    
    /**
     * Example: Generic API call with comprehensive error handling
     */
    fun <T> handleApiCall(
        call: suspend () -> ApiResult<T>,
        onSuccess: (T) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            when (val result = call()) {
                is ApiResult.Success -> {
                    onSuccess(result.data)
                    _uiState.value = UiState.Success("Operation completed successfully")
                }
                
                is ApiResult.SystemMaintenance -> {
                    _uiState.value = UiState.SystemMaintenance(
                        message = result.message,
                        retryAfterSeconds = result.throttleSeconds
                    )
                }
                
                is ApiResult.Throttled -> {
                    _uiState.value = UiState.Throttled(result.throttleSeconds)
                }
                
                is ApiResult.Error -> {
                    _uiState.value = UiState.Error(result.message)
                }
            }
        }
    }
}