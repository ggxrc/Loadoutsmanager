package com.ads.loadoutsmanager.data.repository

import com.ads.loadoutsmanager.data.api.BungieApiService
import com.ads.loadoutsmanager.data.auth.SecureTokenStorage
import com.ads.loadoutsmanager.data.model.LinkedProfiles
import com.ads.loadoutsmanager.data.model.UserMembership
import com.ads.loadoutsmanager.data.model.getPrimaryMembership
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for managing authentication and user membership
 */
class AuthRepository(
    private val bungieApiService: BungieApiService,
    private val tokenStorage: SecureTokenStorage
) {
    
    /**
     * Verify and resolve Cross-Save membership
     * Returns the primary membership to use for API calls
     * 
     * @param membershipType Initial membership type (from OAuth)
     * @param membershipId Initial membership ID (from OAuth)
     * @return Primary membership to use (may be different due to Cross-Save)
     */
    suspend fun resolveCrossSaveMembership(
        membershipType: Int,
        membershipId: String
    ): Result<UserMembership> = withContext(Dispatchers.IO) {
        try {
            val response = bungieApiService.getLinkedProfiles(
                membershipType = membershipType,
                destinyMembershipId = membershipId
            )
            
            if (response.isSuccess && response.Response != null) {
                val linkedProfiles = response.Response
                val primaryMembership = linkedProfiles.getPrimaryMembership()
                
                if (primaryMembership != null) {
                    // Save the primary membership for future use
                    tokenStorage.saveTokens(
                        accessToken = tokenStorage.getAccessToken() ?: "",
                        refreshToken = tokenStorage.getRefreshToken() ?: "",
                        expiresIn = (tokenStorage.getTokenExpiry() - System.currentTimeMillis()) / 1000,
                        membershipId = primaryMembership.membershipId
                    )
                    
                    return@withContext Result.success(primaryMembership)
                } else {
                    return@withContext Result.failure(
                        Exception("No valid Destiny membership found")
                    )
                }
            } else {
                return@withContext Result.failure(
                    Exception("Failed to get linked profiles: ${response.ErrorStatus}")
                )
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Get the currently stored membership
     */
    fun getCurrentMembership(): UserMembership? {
        val membershipId = tokenStorage.getMembershipId()
        val membershipType = tokenStorage.getMembershipType()
        val displayName = tokenStorage.getDisplayName()
        
        return if (membershipId != null && membershipType != null && displayName != null) {
            UserMembership(
                membershipId = membershipId,
                membershipType = membershipType,
                displayName = displayName,
                bungieGlobalDisplayName = null,
                bungieGlobalDisplayNameCode = null,
                crossSaveOverride = 0,
                applicableMembershipTypes = null,
                isPublic = true,
                membershipFlags = 0
            )
        } else null
    }
    
    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return tokenStorage.isAuthenticated()
    }
    
    /**
     * Logout and clear all stored data
     */
    fun logout() {
        tokenStorage.clearTokens()
    }
}