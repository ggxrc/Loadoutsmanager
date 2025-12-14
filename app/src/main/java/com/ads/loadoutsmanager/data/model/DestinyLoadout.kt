package com.ads.loadoutsmanager.data.model

/**
 * Represents a Destiny 2 loadout with equipment and subclass configuration
 * Loadouts are character-specific and include cosmetic customizations
 */
data class DestinyLoadout(
    val id: String,
    val name: String,
    val description: String? = null,
    val characterId: String,
    val equipment: List<DestinyItem>,
    val subclass: SubclassInfo? = null, // Subclass configuration for the loadout
    val isEquipped: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
