package com.ads.loadoutsmanager.data.model

/**
 * Represents a Destiny 2 subclass configuration
 * Used for theme selection and build tracking
 */
data class SubclassInfo(
    val subclassHash: Long,
    val damageType: DamageType,
    val superHash: Long? = null,
    val aspectHashes: List<Long> = emptyList(),
    val fragmentHashes: List<Long> = emptyList(),
    val grenadeHash: Long? = null,
    val meleeHash: Long? = null,
    val classAbilityHash: Long? = null
)

