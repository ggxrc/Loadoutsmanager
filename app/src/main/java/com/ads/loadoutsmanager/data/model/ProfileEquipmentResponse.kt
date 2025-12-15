package com.ads.loadoutsmanager.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Equipment response from Bungie API
 * Component 205: CharacterEquipment
 * Component 300: ItemInstances
 */
@JsonClass(generateAdapter = true)
data class ProfileEquipmentResponse(
    @Json(name = "characterEquipment") val characterEquipment: CharacterEquipmentComponent?,
    @Json(name = "itemComponents") val itemComponents: ItemComponentsSet?
)

@JsonClass(generateAdapter = true)
data class CharacterEquipmentComponent(
    @Json(name = "data") val data: Map<String, EquipmentSet>?
)

@JsonClass(generateAdapter = true)
data class EquipmentSet(
    @Json(name = "items") val items: List<DestinyItemComponent>?
)

@JsonClass(generateAdapter = true)
data class DestinyItemComponent(
    @Json(name = "itemHash") val itemHash: Long,
    @Json(name = "itemInstanceId") val itemInstanceId: String?,
    @Json(name = "quantity") val quantity: Int,
    @Json(name = "bindStatus") val bindStatus: Int,
    @Json(name = "location") val location: Int,
    @Json(name = "bucketHash") val bucketHash: Long,
    @Json(name = "transferStatus") val transferStatus: Int,
    @Json(name = "lockable") val lockable: Boolean,
    @Json(name = "state") val state: Int,
    @Json(name = "dismantlePermission") val dismantlePermission: Int?,
    @Json(name = "isWrapper") val isWrapper: Boolean?
)

@JsonClass(generateAdapter = true)
data class ItemComponentsSet(
    @Json(name = "instances") val instances: ItemInstancesComponent?,
    @Json(name = "stats") val stats: ItemStatsComponent?,
    @Json(name = "sockets") val sockets: ItemSocketsComponent?
)

@JsonClass(generateAdapter = true)
data class ItemInstancesComponent(
    @Json(name = "data") val data: Map<String, DestinyItemInstance>?
)

@JsonClass(generateAdapter = true)
data class DestinyItemInstance(
    @Json(name = "damageType") val damageType: Int,
    @Json(name = "damageTypeHash") val damageTypeHash: Long?,
    @Json(name = "primaryStat") val primaryStat: DestinyItemStat?,
    @Json(name = "itemLevel") val itemLevel: Int,
    @Json(name = "quality") val quality: Int,
    @Json(name = "isEquipped") val isEquipped: Boolean,
    @Json(name = "canEquip") val canEquip: Boolean,
    @Json(name = "equipRequiredLevel") val equipRequiredLevel: Int,
    @Json(name = "unlockHashesRequiredToEquip") val unlockHashesRequiredToEquip: List<Long>?,
    @Json(name = "cannotEquipReason") val cannotEquipReason: Int?,
    @Json(name = "energy") val energy: DestinyItemEnergy?
)

@JsonClass(generateAdapter = true)
data class DestinyItemStat(
    @Json(name = "statHash") val statHash: Long,
    @Json(name = "value") val value: Int
)

@JsonClass(generateAdapter = true)
data class DestinyItemEnergy(
    @Json(name = "energyTypeHash") val energyTypeHash: Long,
    @Json(name = "energyType") val energyType: Int,
    @Json(name = "energyCapacity") val energyCapacity: Int,
    @Json(name = "energyUsed") val energyUsed: Int,
    @Json(name = "energyUnused") val energyUnused: Int
)

@JsonClass(generateAdapter = true)
data class ItemStatsComponent(
    @Json(name = "data") val data: Map<String, DestinyItemStats>?
)

@JsonClass(generateAdapter = true)
data class DestinyItemStats(
    @Json(name = "stats") val stats: Map<String, DestinyItemStat>?
)

@JsonClass(generateAdapter = true)
data class ItemSocketsComponent(
    @Json(name = "data") val data: Map<String, DestinyItemSockets>?
)

@JsonClass(generateAdapter = true)
data class DestinyItemSockets(
    @Json(name = "sockets") val sockets: List<DestinyItemSocket>?
)

@JsonClass(generateAdapter = true)
data class DestinyItemSocket(
    @Json(name = "plugHash") val plugHash: Long?,
    @Json(name = "isEnabled") val isEnabled: Boolean,
    @Json(name = "isVisible") val isVisible: Boolean,
    @Json(name = "enableFailIndexes") val enableFailIndexes: List<Int>?
)

/**
 * Extension to convert to simple DestinyItem model
 */
fun DestinyItemComponent.toDestinyItem(location: ItemLocation): DestinyItem {
    return DestinyItem(
        itemInstanceId = itemInstanceId ?: "",
        itemHash = itemHash,
        bucketHash = bucketHash,
        location = location,
        transferStatus = transferStatus,
        lockable = lockable,
        state = state
    )
}

