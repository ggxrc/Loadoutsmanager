package com.ads.loadoutsmanager.data.api

/**
 * Generic wrapper for Bungie API responses (Envelope Pattern)
 * All Bungie API responses follow this structure
 */
data class BungieResponse<T>(
    val Response: T? = null,
    val ErrorCode: Int = 0,
    val ThrottleSeconds: Int = 0,
    val ErrorStatus: String? = null,
    val Message: String? = null,
    val MessageData: Map<String, String>? = null
) {
    /**
     * Check if the response was successful
     * ErrorCode 1 = Success
     */
    val isSuccess: Boolean
        get() = ErrorCode == 1
    
    /**
     * Check if the response failed
     */
    val isError: Boolean
        get() = ErrorCode != 1
    
    /**
     * Check if API is under maintenance
     */
    val isSystemDisabled: Boolean
        get() = ErrorCode == 5
    
    /**
     * Check if request is being throttled
     */
    val isThrottled: Boolean
        get() = ThrottleSeconds > 0
    
    /**
     * Get the response data or throw an exception
     */
    fun getOrThrow(): T {
        if (isSuccess && Response != null) {
            return Response
        }
        throw BungieApiException(
            errorCode = ErrorCode,
            message = ErrorStatus ?: Message ?: "Unknown error",
            throttleSeconds = ThrottleSeconds
        )
    }
    
    /**
     * Get the response data or return null
     */
    fun getOrNull(): T? {
        return if (isSuccess) Response else null
    }
}

/**
 * Exception for Bungie API errors
 */
class BungieApiException(
    val errorCode: Int,
    message: String,
    val throttleSeconds: Int = 0
) : Exception("Bungie API Error ($errorCode): $message")

/**
 * Common Bungie Error Codes
 */
object BungieErrorCodes {
    const val SUCCESS = 1
    const val TRANSPORT_EXCEPTION = 2
    const val UNHANDLED_EXCEPTION = 3
    const val NOT_FOUND = 4
    const val SYSTEM_DISABLED = 5
    const val SYSTEM_OVERLOADED = 6
    const val THROTTLE_LIMIT_EXCEEDED = 7
    const val PERMISSION_DENIED = 99
    const val API_INVALID_OR_EXPIRED_KEY = 2101
    const val API_KEY_MISSING_FROM_REQUEST = 2102
    const val ACCESS_TOKEN_REQUIRED = 2106
    const val ACCESS_TOKEN_EXPIRED = 2110
    const val DESTINY_ACCOUNT_NOT_FOUND = 1601
    const val DESTINY_ITEM_NOT_FOUND = 1623
    const val DESTINY_ITEM_UNEQUIPPABLE = 1625
    const val DESTINY_NO_ROOM_IN_DESTINATION = 1642
}