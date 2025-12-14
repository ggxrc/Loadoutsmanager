package com.ads.loadoutsmanager.data.repository

import com.ads.loadoutsmanager.data.api.BungieApiService
import com.ads.loadoutsmanager.data.network.NetworkModule

/**
 * Repository class to handle Bungie API calls
 * This is where you'll implement the business logic for loadout management
 */
class BungieRepository {
    
    // Token provider - you'll need to implement OAuth2 authentication to get this token
    private var accessToken: String? = null
    
    // Create the API service with token provider
    private val apiService: BungieApiService = NetworkModule.createBungieApiService {
        accessToken
    }
    
    /**
     * Set the OAuth2 access token for authenticated requests
     */
    fun setAccessToken(token: String) {
        accessToken = token
    }
    
    /**
     * Get user's Destiny 2 profile
     * Components: 100 = Profiles, 200 = Characters, 201 = CharacterInventories, 
     *            205 = CharacterEquipment, 102 = ProfileInventories
     */
    suspend fun getDestinyProfile(
        membershipType: Int,
        destinyMembershipId: String
    ) = try {
        val response = apiService.getProfile(
            membershipType = membershipType,
            destinyMembershipId = destinyMembershipId,
            components = "100,200,201,205,102" // Profile, Characters, Inventories, Equipment
        )
        response
    } catch (e: Exception) {
        throw BungieApiException("Failed to get profile: ${e.message}", e)
    }
    
    /**
     * Get character details including equipment
     */
    suspend fun getCharacter(
        membershipType: Int,
        destinyMembershipId: String,
        characterId: String
    ) = try {
        val response = apiService.getCharacter(
            membershipType = membershipType,
            destinyMembershipId = destinyMembershipId,
            characterId = characterId,
            components = "200,201,205" // Character, Inventory, Equipment
        )
        response
    } catch (e: Exception) {
        throw BungieApiException("Failed to get character: ${e.message}", e)
    }
    
    /**
     * Equip an item on a character
     */
    suspend fun equipItem(
        itemId: String,
        characterId: String,
        membershipType: Int
    ) = try {
        val request = com.ads.loadoutsmanager.data.api.EquipItemRequest(
            itemId = itemId,
            characterId = characterId,
            membershipType = membershipType
        )
        apiService.equipItem(request)
    } catch (e: Exception) {
        throw BungieApiException("Failed to equip item: ${e.message}", e)
    }
    
    /**
     * Transfer item between character and vault
     */
    suspend fun transferItem(
        itemReferenceHash: Long,
        stackSize: Int,
        transferToVault: Boolean,
        itemId: String,
        characterId: String,
        membershipType: Int
    ) = try {
        val request = com.ads.loadoutsmanager.data.api.TransferItemRequest(
            itemReferenceHash = itemReferenceHash,
            stackSize = stackSize,
            transferToVault = transferToVault,
            itemId = itemId,
            characterId = characterId,
            membershipType = membershipType
        )
        apiService.transferItem(request)
    } catch (e: Exception) {
        throw BungieApiException("Failed to transfer item: ${e.message}", e)
    }
    
    /**
     * Equip multiple items at once (loadout)
     */
    suspend fun equipLoadout(
        itemIds: List<String>,
        characterId: String,
        membershipType: Int
    ) = try {
        val request = com.ads.loadoutsmanager.data.api.EquipItemsRequest(
            itemIds = itemIds,
            characterId = characterId,
            membershipType = membershipType
        )
        apiService.equipItems(request)
    } catch (e: Exception) {
        throw BungieApiException("Failed to equip loadout: ${e.message}", e)
    }
}

/**
 * Custom exception for Bungie API errors
 */
class BungieApiException(message: String, cause: Throwable? = null) : Exception(message, cause)