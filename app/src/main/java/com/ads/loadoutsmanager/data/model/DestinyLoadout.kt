package com.ads.loadoutsmanager.data.model

/**
 * Represents a Destiny 2 loadout with equipment per character
 */
data class DestinyLoadout(
    val id: String,
    val name: String,
    val description: String? = null,
    val characterId: String,
    val subclassHash: Long,
    val equipment: LoadoutEquipment,
    val isEquipped: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class LoadoutEquipment(
    val kineticWeapon: DestinyItem? = null,
    val energyWeapon: DestinyItem? = null,
    val powerWeapon: DestinyItem? = null,
    val helmet: DestinyItem? = null,
    val gauntlets: DestinyItem? = null,
    val chestArmor: DestinyItem? = null,
    val legArmor: DestinyItem? = null,
    val classItem: DestinyItem? = null
)
