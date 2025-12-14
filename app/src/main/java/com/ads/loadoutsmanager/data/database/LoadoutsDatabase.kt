package com.ads.loadoutsmanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ads.loadoutsmanager.data.database.dao.ItemDao
import com.ads.loadoutsmanager.data.database.dao.LoadoutDao
import com.ads.loadoutsmanager.data.database.entity.ItemEntity
import com.ads.loadoutsmanager.data.database.entity.LoadoutEntity

/**
 * Room database for the Loadouts Manager app
 * Stores loadouts and items locally for offline access
 */
@Database(
    entities = [LoadoutEntity::class, ItemEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LoadoutsDatabase : RoomDatabase() {
    
    abstract fun loadoutDao(): LoadoutDao
    abstract fun itemDao(): ItemDao
    
    companion object {
        @Volatile
        private var INSTANCE: LoadoutsDatabase? = null
        
        fun getDatabase(context: Context): LoadoutsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LoadoutsDatabase::class.java,
                    "loadouts_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
