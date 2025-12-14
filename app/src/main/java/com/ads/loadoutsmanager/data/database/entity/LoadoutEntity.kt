package com.ads.loadoutsmanager.data.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ads.loadoutsmanager.data.model.DamageType

/**
 * Room entity for storing loadouts
 * Loadouts are character-specific and include subclass configuration
 */
@Entity(tableName = "loadouts")
data class LoadoutEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String? = null,
    val characterId: String,
    val itemIds: String, // Comma-separated list of item instance IDs
    @Embedded(prefix = "subclass_")
    val subclass: SubclassEntity? = null,
    val isEquipped: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Embedded entity for subclass information
 */
data class SubclassEntity(
    val subclassHash: Long,
    val damageType: DamageType,
    val superHash: Long? = null,
    val aspectHashes: String? = null, // Comma-separated Long values
    val fragmentHashes: String? = null, // Comma-separated Long values
    val grenadHash: Long? = null,
    val meleeHash: Long? = null,
    val classAbilityHash: Long? = null
)
