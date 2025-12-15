package com.ads.loadoutsmanager.data.auth

import android.content.Context
import android.content.SharedPreferences

/**
 * Secure storage for OAuth2 tokens using regular SharedPreferences
 * TODO: Switch to EncryptedSharedPreferences in production
 */
class SecureTokenStorage(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "bungie_secure_tokens"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
        private const val KEY_MEMBERSHIP_ID = "membership_id"
        private const val KEY_MEMBERSHIP_TYPE = "membership_type"
        private const val KEY_DISPLAY_NAME = "display_name"
    }
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Save OAuth2 tokens securely
     */
    fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresIn: Long,
        membershipId: String? = null,
        membershipType: Int? = null,
        displayName: String? = null
    ) {
        val expiryTime = System.currentTimeMillis() + (expiresIn * 1000)
        
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putLong(KEY_TOKEN_EXPIRY, expiryTime)
            membershipId?.let { putString(KEY_MEMBERSHIP_ID, it) }
            membershipType?.let { putInt(KEY_MEMBERSHIP_TYPE, it) }
            displayName?.let { putString(KEY_DISPLAY_NAME, it) }
            commit()  // Synchronous save
        }
    }
    
    /**
     * Get access token
     */
    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }
    
    /**
     * Get refresh token
     */
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }
    
    /**
     * Get token expiry time
     */
    fun getTokenExpiry(): Long {
        return sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0)
    }
    
    /**
     * Check if access token is expired
     */
    fun isTokenExpired(): Boolean {
        val expiryTime = getTokenExpiry()
        return expiryTime == 0L || System.currentTimeMillis() >= expiryTime
    }
    
    /**
     * Get membership ID
     */
    fun getMembershipId(): String? {
        return sharedPreferences.getString(KEY_MEMBERSHIP_ID, null)
    }
    
    /**
     * Get membership type
     */
    fun getMembershipType(): Int? {
        val type = sharedPreferences.getInt(KEY_MEMBERSHIP_TYPE, -1)
        return if (type != -1) type else null
    }
    
    /**
     * Get display name
     */
    fun getDisplayName(): String? {
        return sharedPreferences.getString(KEY_DISPLAY_NAME, null)
    }
    
    /**
     * Clear all tokens (logout)
     */
    fun clearTokens() {
        sharedPreferences.edit().clear().apply()
    }
    
    /**
     * Check if user is authenticated (has valid tokens)
     */
    fun isAuthenticated(): Boolean {
        return getAccessToken() != null && getRefreshToken() != null
    }
}