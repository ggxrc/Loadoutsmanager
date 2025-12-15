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
     * Gets the user's Destiny memberships from GetMembershipsForCurrentUser API
     * Then resolves the primary Destiny membership (considering Cross-Save)
     */
    suspend fun resolveCrossSaveMembership(): Result<UserMembership> = withContext(Dispatchers.IO) {
        try {
            // Get current user's memberships (this includes all platforms)
            val membershipsResponse = bungieApiService.getMembershipsForCurrentUser()

            if (membershipsResponse.isSuccess && membershipsResponse.Response != null) {
                val membershipsData = membershipsResponse.Response

                // Get the primary membership (considering Cross-Save)
                val primaryMembership = if (membershipsData.destinyMemberships.isNotEmpty()) {
                    // Find the membership with crossSaveOverride != 0, or the first one
                    membershipsData.destinyMemberships.find { it.crossSaveOverride != 0 }
                        ?: membershipsData.destinyMemberships.firstOrNull()
                } else {
                    null
                }

                if (primaryMembership != null) {
                    // Save the primary membership for future use
                    tokenStorage.saveTokens(
                        accessToken = tokenStorage.getAccessToken() ?: "",
                        refreshToken = tokenStorage.getRefreshToken() ?: "",
                        expiresIn = (tokenStorage.getTokenExpiry() - System.currentTimeMillis()) / 1000,
                        membershipId = primaryMembership.membershipId,
                        membershipType = primaryMembership.membershipType,
                        displayName = primaryMembership.displayName
                    )

                    return@withContext Result.success(primaryMembership)
                } else {
                    return@withContext Result.failure(
                        Exception("No valid Destiny membership found. Make sure you have played Destiny 2.")
                    )
                }
            } else {
                return@withContext Result.failure(
                    Exception("Failed to get memberships: ${membershipsResponse.ErrorStatus} - ${membershipsResponse.Message}")
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error resolving Cross-Save membership", e)
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