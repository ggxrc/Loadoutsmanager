package com.ads.loadoutsmanager.data.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ads.loadoutsmanager.data.model.ItemLocation

/**
 * Room entity for storing Destiny items
 * Represents equipment with cosmetic customizations
 */
@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey
    val itemInstanceId: String,
    val itemHash: Long,
    val bucketHash: Long,
    val location: ItemLocation,
    val iconUrl: String? = null,
    val transferStatus: Int = 0,
    val lockable: Boolean = true,
    val state: Int = 0,
    @Embedded(prefix = "cosmetics_")
    val cosmetics: ItemCosmeticsEntity? = null
)

/**
 * Embedded entity for item cosmetics
 */
data class ItemCosmeticsEntity(
    val ornamentHash: Long? = null,
    val shaderHash: Long? = null
)
