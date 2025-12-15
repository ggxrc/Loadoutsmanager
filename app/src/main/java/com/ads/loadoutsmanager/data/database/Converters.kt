package com.ads.loadoutsmanager.data.database

import androidx.room.TypeConverter
import com.ads.loadoutsmanager.data.model.DamageType
import com.ads.loadoutsmanager.data.model.ItemLocation

/**
 * Type converters for Room database
 * Converts complex types to primitives for database storage
 */
class Converters {
    
    @TypeConverter
    fun fromItemLocation(location: ItemLocation): String {
        return location.name
    }
    
    @TypeConverter
    fun toItemLocation(value: String): ItemLocation {
        return ItemLocation.valueOf(value)
    }
    
    @TypeConverter
    fun fromDamageType(damageType: DamageType): String {
        return damageType.name
    }
    
    @TypeConverter
    fun toDamageType(value: String): DamageType {
        return DamageType.valueOf(value)
    }
    
    @TypeConverter
    fun fromLongList(value: List<Long>?): String? {
        return value?.joinToString(",")
    }
    
    @TypeConverter
    fun toLongList(value: String?): List<Long> {
        return value?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList()
    }
}
