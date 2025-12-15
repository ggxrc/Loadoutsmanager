package com.ads.loadoutsmanager

import android.app.Application
import com.ads.loadoutsmanager.data.api.NetworkModule
import com.ads.loadoutsmanager.data.api.OAuth2Manager
import com.ads.loadoutsmanager.data.api.TokenRefreshAuthenticator
import com.ads.loadoutsmanager.data.auth.SecureTokenStorage
import com.ads.loadoutsmanager.data.database.LoadoutsDatabase
import com.ads.loadoutsmanager.data.repository.AuthRepository
import com.ads.loadoutsmanager.data.repository.LoadoutRepository
import net.openid.appauth.AuthorizationService

/**
 * Application class for dependency initialization
 * Uses credentials from BungieConfig (loaded from local.properties)
 */
class LoadoutsApplication : Application() {
    
    // Lazy initialization of dependencies
    val tokenStorage: SecureTokenStorage by lazy {
        SecureTokenStorage(this)
    }
    
    val database: LoadoutsDatabase by lazy {
        LoadoutsDatabase.getDatabase(this)
    }
    
    private val oauth2Manager by lazy {
        OAuth2Manager(this, tokenStorage)
    }
    
    private val authorizationService by lazy {
        AuthorizationService(this)
    }
    
    private val tokenRefreshAuthenticator by lazy {
        TokenRefreshAuthenticator(
            tokenStorage = tokenStorage,
            oauth2Manager = oauth2Manager,
            authorizationService = authorizationService
        )
    }
    
    // API Service with dynamic token and automatic refresh
    val bungieApiService by lazy {
        NetworkModule.createBungieApiService(
            getAccessToken = { tokenStorage.getAccessToken() },
            authenticator = tokenRefreshAuthenticator
        )
    }

    val manifestService by lazy {
        NetworkModule.createManifestService()
    }
    
    // Repositories
    val authRepository by lazy {
        AuthRepository(bungieApiService, tokenStorage)
    }
    
    fun createLoadoutRepository(membershipType: Int, membershipId: String): LoadoutRepository {
        return LoadoutRepository(
            bungieApiService = bungieApiService,
            database = database,
            membershipType = membershipType,
            membershipId = membershipId
        )
    }
    
    override fun onTerminate() {
        super.onTerminate()
        authorizationService.dispose()
    }
}
