package com.ads.loadoutsmanager.data.model

/**
 * Represents a Destiny 2 item (weapon, armor, etc.)
 * Includes cosmetic customizations (ornaments and shaders)
 */
data class DestinyItem(
    val itemInstanceId: String,
    val itemHash: Long,
    val bucketHash: Long,
    val location: ItemLocation,
    val transferStatus: Int = 0,
    val lockable: Boolean = true,
    val state: Int = 0,
    val cosmetics: ItemCosmetics? = null, // Weapon/armor ornament and shader
    val iconUrl: String? = null // Icon path from Bungie CDN
)

enum class ItemLocation {
    EQUIPPED,
    INVENTORY,
    VAULT,
    POSTMASTER
}
