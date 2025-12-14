package com.ads.loadoutsmanager.data.api

import retrofit2.Response
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
     * Get user's Destiny 2 profile
     * Requires OAuth2 authentication
     */
    @GET("Destiny2/{membershipType}/Profile/{destinyMembershipId}/")
    suspend fun getProfile(
        @Path("membershipType") membershipType: Int,
        @Path("destinyMembershipId") destinyMembershipId: String,
        @Query("components") components: String
    ): Response<DestinyProfileResponse>
    
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
    ): Response<DestinyCharacterResponse>
    
    /**
     * Equip item on character
     * Requires OAuth2 authentication with write permissions
     */
    @POST("Destiny2/Actions/Items/EquipItem/")
    suspend fun equipItem(
        @Body request: EquipItemRequest
    ): Response<EquipItemResponse>
    
    /**
     * Transfer item between character and vault
     * Requires OAuth2 authentication with write permissions
     */
    @POST("Destiny2/Actions/Items/TransferItem/")
    suspend fun transferItem(
        @Body request: TransferItemRequest
    ): Response<TransferItemResponse>
    
    /**
     * Equip multiple items at once
     * Requires OAuth2 authentication with write permissions
     */
    @POST("Destiny2/Actions/Items/EquipItems/")
    suspend fun equipItems(
        @Body request: EquipItemsRequest
    ): Response<EquipItemsResponse>
}

// Request/Response models for API calls
data class DestinyProfileResponse(
    val Response: ProfileData?,
    val ErrorCode: Int,
    val ErrorStatus: String?
)

data class ProfileData(
    val characters: Map<String, Any>?,
    val characterEquipment: Map<String, Any>?,
    val characterInventories: Map<String, Any>?,
    val profileInventory: Map<String, Any>?
)

data class DestinyCharacterResponse(
    val Response: CharacterData?,
    val ErrorCode: Int,
    val ErrorStatus: String?
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

data class EquipItemResponse(
    val Response: Int,
    val ErrorCode: Int,
    val ErrorStatus: String?
)

data class TransferItemRequest(
    val itemReferenceHash: Long,
    val stackSize: Int,
    val transferToVault: Boolean,
    val itemId: String,
    val characterId: String,
    val membershipType: Int
)

data class TransferItemResponse(
    val Response: Int,
    val ErrorCode: Int,
    val ErrorStatus: String?
)

data class EquipItemsRequest(
    val itemIds: List<String>,
    val characterId: String,
    val membershipType: Int
)

data class EquipItemsResponse(
    val Response: EquipResultSet?,
    val ErrorCode: Int,
    val ErrorStatus: String?
)

data class EquipResultSet(
    val equipResults: List<EquipResult>
)

data class EquipResult(
    val itemInstanceId: String,
    val equipStatus: Int
)
