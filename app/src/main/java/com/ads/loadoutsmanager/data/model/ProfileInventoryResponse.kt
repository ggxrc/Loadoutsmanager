package com.ads.loadoutsmanager.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Inventory response from Bungie API
 * Component 201: CharacterInventories
 * Component 102: ProfileInventory (Vault)
 */
@JsonClass(generateAdapter = true)
data class ProfileInventoryResponse(
    @Json(name = "characterInventories") val characterInventories: CharacterInventoriesComponent?,
    @Json(name = "profileInventory") val profileInventory: ProfileInventoryComponent?,
    @Json(name = "itemComponents") val itemComponents: ItemComponentsSet?
)

@JsonClass(generateAdapter = true)
data class CharacterInventoriesComponent(
    @Json(name = "data") val data: Map<String, InventorySet>?
)

@JsonClass(generateAdapter = true)
data class ProfileInventoryComponent(
    @Json(name = "data") val data: InventoryData?
)

@JsonClass(generateAdapter = true)
data class InventorySet(
    @Json(name = "items") val items: List<DestinyItemComponent>?
)

@JsonClass(generateAdapter = true)
data class InventoryData(
    @Json(name = "items") val items: List<DestinyItemComponent>?
)

