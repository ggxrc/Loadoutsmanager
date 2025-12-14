package com.ads.loadoutsmanager.data.api

/**
 * Configuration for Bungie API and OAuth2
 * 
 * IMPORTANT: Before using this app, you must:
 * 1. Register your app at https://www.bungie.net/en/Application
 * 2. Get your API Key and Client ID
 * 3. Configure your OAuth2 redirect URI to: com.ads.loadoutsmanager://oauth2redirect
 * 4. Replace the placeholder values below with your actual credentials
 */
object BungieConfig {
    
    /**
     * Your Bungie API Key
     * Get it from: https://www.bungie.net/en/Application
     */
    const val API_KEY = "YOUR_API_KEY_HERE"
    
    /**
     * Your OAuth2 Client ID
     * Get it from: https://www.bungie.net/en/Application
     */
    const val CLIENT_ID = "YOUR_CLIENT_ID_HERE"
    
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
     * Check if configuration is complete
     */
    fun isConfigured(): Boolean {
        return API_KEY != "YOUR_API_KEY_HERE" && CLIENT_ID != "YOUR_CLIENT_ID_HERE"
    }
}
