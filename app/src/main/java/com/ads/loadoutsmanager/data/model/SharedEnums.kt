package com.ads.loadoutsmanager.data.model

/**
 * Shared enums for item and subclass data
 */

enum class DamageType {
    KINETIC,
    SOLAR,
    ARC,
    VOID,
    STASIS,
    STRAND
}

enum class TierType {
    COMMON,
    UNCOMMON,
    RARE,
    LEGENDARY,
    EXOTIC
}

/**
 * Item categories for organization
 */
enum class ItemCategory {
    WEAPON,
    ARMOR;

    fun getSubcategories(): List<ItemSubcategory> {
        return when (this) {
            WEAPON -> listOf(
                ItemSubcategory.KINETIC,
                ItemSubcategory.ENERGY,
                ItemSubcategory.POWER
            )
            ARMOR -> listOf(
                ItemSubcategory.HELMET,
                ItemSubcategory.GAUNTLETS,
                ItemSubcategory.CHEST,
                ItemSubcategory.LEGS,
                ItemSubcategory.CLASS_ITEM
            )
        }
    }
}

enum class ItemSubcategory(val displayName: String, val bucketHash: Long) {
    // Weapons
    KINETIC("Kinetic", 1498876634L),
    ENERGY("Energy", 2465295065L),
    POWER("Power", 953998645L),

    // Armor
    HELMET("Helmet", 3448274439L),
    GAUNTLETS("Gauntlets", 3551918588L),
    CHEST("Chest", 14239492L),
    LEGS("Legs", 20886954L),
    CLASS_ITEM("Class Item", 1585787867L);

    companion object {
        fun fromBucketHash(hash: Long): ItemSubcategory? {
            return values().find { it.bucketHash == hash }
        }
    }
}

