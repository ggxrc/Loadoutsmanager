package com.ads.loadoutsmanager.data.model

/**
 * Represents a Destiny 2 loadout with equipment
 */
data class DestinyLoadout(
    val id: String,
    val name: String,
    val description: String? = null,
    val characterId: String,
    val equipment: List<DestinyItem>,
    val isEquipped: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
