package com.ads.loadoutsmanager.data.model

/**
 * Represents a Destiny 2 item (weapon, armor, etc.)
 */
data class DestinyItem(
    val itemInstanceId: String,
    val itemHash: Long,
    val bucketHash: Long,
    val location: ItemLocation,
    val transferStatus: Int = 0,
    val lockable: Boolean = true,
    val state: Int = 0,
    val perks: List<Long> = emptyList(),
    val stats: Map<String, Int> = emptyMap(),
    val cosmetics: ItemCosmetics? = null,
    val characterId: String? = null
)

data class ItemCosmetics(
    val ornamentHash: Long? = null,
    val shaderHash: Long? = null
)

enum class ItemLocation {
    EQUIPPED,
    INVENTORY,
    VAULT,
    POSTMASTER
}
