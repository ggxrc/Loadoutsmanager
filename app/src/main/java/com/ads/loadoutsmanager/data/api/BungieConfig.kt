package com.ads.loadoutsmanager.data.api

import com.ads.loadoutsmanager.BuildConfig

/**
 * Configuration for Bungie API endpoints and OAuth2
 *
 * API credentials are loaded from local.properties via BuildConfig:
 * - bungie.api.key → BuildConfig.BUNGIE_API_KEY
 * - bungie.client.id → BuildConfig.BUNGIE_CLIENT_ID
 * - bungie.client.secret → BuildConfig.BUNGIE_CLIENT_SECRET
 */
object BungieConfig {
    
    /**
     * Bungie API Base URL
     */
    const val BASE_URL = "https://www.bungie.net/Platform/"
    
    /**
     * OAuth2 Authorization Endpoint
     */
    const val AUTHORIZATION_ENDPOINT = "https://www.bungie.net/en/OAuth/Authorize"
    
    /**
     * OAuth2 Token Endpoint
     */
    const val TOKEN_ENDPOINT = "https://www.bungie.net/platform/app/oauth/token/"
    
    /**
     * OAuth2 Redirect URI (must match Bungie app configuration)
     */
    const val REDIRECT_URI = "com.ads.loadoutsmanager://oauth2redirect"

    /**
     * Get API Key from BuildConfig
     */
    val apiKey: String
        get() = BuildConfig.BUNGIE_API_KEY

    /**
     * Get OAuth Client ID from BuildConfig
     */
    val clientId: String
        get() = BuildConfig.BUNGIE_CLIENT_ID

    /**
     * Get OAuth Client Secret from BuildConfig
     */
    val clientSecret: String
        get() = BuildConfig.BUNGIE_CLIENT_SECRET

    /**
     * Check if API Key is configured
     */
    fun isApiKeyConfigured(): Boolean {
        return apiKey.isNotEmpty()
    }
    
    /**
     * Check if OAuth2 is fully configured
     */
    fun isOAuth2Configured(): Boolean {
        return isApiKeyConfigured() && 
               clientId.isNotEmpty() &&
               clientSecret.isNotEmpty()
    }
}
