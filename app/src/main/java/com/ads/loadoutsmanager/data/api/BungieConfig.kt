package com.ads.loadoutsmanager.data.api

/**
 * Configuration for Bungie API and OAuth2
 * 
 * IMPORTANT: Before using this app, you must:
 * 1. Register your app at https://www.bungie.net/en/Application
 * 2. Get your **API Key** (required for all API calls)
 * 3. For OAuth2 features, you'll also need **OAuth Client ID** and **OAuth Client Secret**
 * 4. Configure your OAuth2 redirect URI to: com.ads.loadoutsmanager://oauth2redirect
 * 
 * AUTHENTICATION OPTIONS:
 * 
 * Option 1: API Key Only (Read-only access)
 * - Set API_KEY with your Bungie API key
 * - Leave CLIENT_ID and CLIENT_SECRET as placeholders
 * - You can read data but cannot perform write operations (equip items, transfer, etc.)
 * 
 * Option 2: Full OAuth2 (Read/Write access)
 * - Set API_KEY, CLIENT_ID, and CLIENT_SECRET
 * - Full functionality including equipping items and vault operations
 * - Required scopes: ReadUserData, ReadDestinyInventoryAndVault, MoveEquipDestinyItems
 * 
 * Note: The user mentioned they only have an API Key. If you don't have Client ID/Secret,
 * you can still use the app in read-only mode to view loadouts and inventory.
 */
object BungieConfig {
    
    /**
     * Your Bungie API Key
     * Get it from: https://www.bungie.net/en/Application
     * Required for all API operations
     */
    const val API_KEY = "YOUR_API_KEY_HERE"
    
    /**
     * Your OAuth2 Client ID (Optional - only needed for OAuth2 authentication)
     * Get it from: https://www.bungie.net/en/Application
     * Required for: Equipping items, transferring items, writing data
     * Leave as placeholder if you only have an API Key
     */
    const val CLIENT_ID = "YOUR_CLIENT_ID_HERE"
    
    /**
     * Your OAuth2 Client Secret (Optional - only needed for OAuth2 authentication)
     * Get it from: https://www.bungie.net/en/Application
     * Required for: OAuth2 token exchange and refresh
     * Leave as placeholder if you only have an API Key
     * 
     * SECURITY NOTE: In production, this should be stored securely on a backend server,
     * not in the Android app. Consider using a backend proxy for OAuth2 operations.
     */
    const val CLIENT_SECRET = "YOUR_CLIENT_SECRET_HERE"
    
    /**
     * OAuth2 Redirect URI
     * This must match the redirect URI configured in your Bungie app
     */
    const val REDIRECT_URI = "com.ads.loadoutsmanager://oauth2redirect"
    
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
     * Check if API Key is configured
     */
    fun isApiKeyConfigured(): Boolean {
        return API_KEY != "YOUR_API_KEY_HERE"
    }
    
    /**
     * Check if OAuth2 is fully configured
     */
    fun isOAuth2Configured(): Boolean {
        return isApiKeyConfigured() && 
               CLIENT_ID != "YOUR_CLIENT_ID_HERE" && 
               CLIENT_SECRET != "YOUR_CLIENT_SECRET_HERE"
    }
    
    /**
     * Legacy method for backward compatibility
     */
    @Deprecated("Use isApiKeyConfigured() or isOAuth2Configured() instead")
    fun isConfigured(): Boolean {
        return isApiKeyConfigured()
    }
}
