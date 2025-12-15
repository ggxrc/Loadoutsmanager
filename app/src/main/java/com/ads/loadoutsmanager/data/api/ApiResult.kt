package com.ads.loadoutsmanager.data.api

/**
 * Sealed class representing API call results with proper error handling
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int) : ApiResult<Nothing>()
    data class SystemMaintenance(val message: String, val throttleSeconds: Int = 0) : ApiResult<Nothing>()
    data class Throttled(val throttleSeconds: Int) : ApiResult<Nothing>()
}

/**
 * Extension function to convert BungieResponse to ApiResult
 * Provides centralized error handling for all API calls
 */
fun <T> BungieResponse<T>.toApiResult(): ApiResult<T> {
    return when {
        // Success
        isSuccess && Response != null -> ApiResult.Success(Response)
        
        // System disabled (maintenance)
        isSystemDisabled -> ApiResult.SystemMaintenance(
            message = ErrorStatus ?: Message ?: "Bungie services are currently undergoing maintenance",
            throttleSeconds = ThrottleSeconds
        )
        
        // Throttled
        isThrottled -> ApiResult.Throttled(ThrottleSeconds)
        
        // Generic error
        else -> ApiResult.Error(
            message = ErrorStatus ?: Message ?: "Unknown error occurred",
            code = ErrorCode
        )
    }
}

/**
 * Execute API call with automatic error handling
 */
suspend fun <T> executeApiCall(
    call: suspend () -> BungieResponse<T>
): ApiResult<T> {
    return try {
        val response = call()
        response.toApiResult()
    } catch (e: Exception) {
        ApiResult.Error(
            message = e.message ?: "Network error occurred",
            code = BungieErrorCodes.TRANSPORT_EXCEPTION
        )
    }
}