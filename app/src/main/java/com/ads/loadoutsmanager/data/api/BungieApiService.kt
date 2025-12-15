package com.ads.loadoutsmanager.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for Destiny 2 Bungie API
 * Base URL: https://www.bungie.net/Platform
 */
interface BungieApiService {
    
    companion object {
        const val BASE_URL = "https://www.bungie.net/Platform/"
        const val API_KEY_HEADER = "X-API-Key"
    }
    
    /**
     * Get linked profiles for the current user (Cross-Save support)
     * Requires OAuth2 authentication
     * Returns all linked platform accounts and Cross-Save status
     */
    @GET("Destiny2/{membershipType}/Profile/{destinyMembershipId}/LinkedProfiles/")
    suspend fun getLinkedProfiles(
        @Path("membershipType") membershipType: Int,
        @Path("destinyMembershipId") destinyMembershipId: String
    ): BungieResponse<com.ads.loadoutsmanager.data.model.LinkedProfiles>
    
    /**
     * Get memberships for the current authenticated user
     * Requires OAuth2 authentication
     * Returns all platform memberships associated with the Bungie account
     */
    @GET("User/GetMembershipsForCurrentUser/")
    suspend fun getMembershipsForCurrentUser(): BungieResponse<com.ads.loadoutsmanager.data.model.UserMembershipsData>

    /**
     * Get user's Destiny 2 profile with characters
     * Component 200: Characters
     * Requires OAuth2 authentication
     */
    @GET("Destiny2/{membershipType}/Profile/{destinyMembershipId}/")
    suspend fun getProfileCharacters(
        @Path("membershipType") membershipType: Int,
        @Path("destinyMembershipId") destinyMembershipId: String,
        @Query("components") components: String = "200"
    ): BungieResponse<com.ads.loadoutsmanager.data.model.ProfileCharactersResponse>

    /**
     * Get character equipment
     * Component 205: CharacterEquipment
     * Component 300: ItemInstances
     * Requires OAuth2 authentication
     */
    @GET("Destiny2/{membershipType}/Profile/{destinyMembershipId}/")
    suspend fun getProfileEquipment(
        @Path("membershipType") membershipType: Int,
        @Path("destinyMembershipId") destinyMembershipId: String,
        @Query("components") components: String = "205,300"
    ): BungieResponse<com.ads.loadoutsmanager.data.model.ProfileEquipmentResponse>

    /**
     * Get character and vault inventories
     * Component 201: CharacterInventories
     * Component 102: ProfileInventory (vault)
     * Component 300: ItemInstances
     * Requires OAuth2 authentication
     */
    @GET("Destiny2/{membershipType}/Profile/{destinyMembershipId}/")
    suspend fun getProfileInventories(
        @Path("membershipType") membershipType: Int,
        @Path("destinyMembershipId") destinyMembershipId: String,
        @Query("components") components: String = "102,201,300"
    ): BungieResponse<com.ads.loadoutsmanager.data.model.ProfileInventoryResponse>

    /**
     * Get character equipment
     * Requires OAuth2 authentication
     */
    @GET("Destiny2/{membershipType}/Profile/{destinyMembershipId}/Character/{characterId}/")
    suspend fun getCharacter(
        @Path("membershipType") membershipType: Int,
        @Path("destinyMembershipId") destinyMembershipId: String,
        @Path("characterId") characterId: String,
        @Query("components") components: String
    ): BungieResponse<CharacterData>
    
    /**
     * Equip item on character
     * Requires OAuth2 authentication with write permissions
     */
    @POST("Destiny2/Actions/Items/EquipItem/")
    suspend fun equipItem(
        @Body request: EquipItemRequest
    ): BungieResponse<Int>
    
    /**
     * Transfer item between character and vault
     * Requires OAuth2 authentication with write permissions
     */
    @POST("Destiny2/Actions/Items/TransferItem/")
    suspend fun transferItem(
        @Body request: TransferItemRequest
    ): BungieResponse<Int>
    
    /**
     * Equip multiple items at once
     * Requires OAuth2 authentication with write permissions
     */
    @POST("Destiny2/Actions/Items/EquipItems/")
    suspend fun equipItems(
        @Body request: EquipItemsRequest
    ): BungieResponse<EquipResultSet>
}

// Request/Response models for API calls
data class ProfileData(
    val characters: Map<String, Any>?,
    val characterEquipment: Map<String, Any>?,
    val characterInventories: Map<String, Any>?,
    val profileInventory: Map<String, Any>?
)

data class CharacterData(
    val equipment: EquipmentData?,
    val inventory: InventoryData?
)

data class EquipmentData(
    val data: Map<String, Any>?
)

data class InventoryData(
    val data: Map<String, Any>?
)

data class EquipItemRequest(
    val itemId: String,
    val characterId: String,
    val membershipType: Int
)

data class TransferItemRequest(
    val itemReferenceHash: Long,
    val stackSize: Int,
    val transferToVault: Boolean,
    val itemId: String,
    val characterId: String,
    val membershipType: Int
)

data class EquipItemsRequest(
    val itemIds: List<String>,
    val characterId: String,
    val membershipType: Int
)

data class EquipResultSet(
    val equipResults: List<EquipResult>
)

data class EquipResult(
    val itemInstanceId: String,
    val equipStatus: Int
)
