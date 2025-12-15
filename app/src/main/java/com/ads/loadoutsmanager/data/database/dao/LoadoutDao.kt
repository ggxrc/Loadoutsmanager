package com.ads.loadoutsmanager.data.database.dao

import androidx.room.*
import com.ads.loadoutsmanager.data.database.entity.LoadoutEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for loadouts
 */
@Dao
interface LoadoutDao {
    
    @Query("SELECT * FROM loadouts WHERE id = :loadoutId")
    suspend fun getLoadout(loadoutId: String): LoadoutEntity?
    
    @Query("SELECT * FROM loadouts WHERE characterId = :characterId ORDER BY updatedAt DESC")
    suspend fun getLoadoutsForCharacter(characterId: String): List<LoadoutEntity>
    
    @Query("SELECT * FROM loadouts WHERE characterId = :characterId ORDER BY updatedAt DESC")
    fun getLoadoutsForCharacterFlow(characterId: String): Flow<List<LoadoutEntity>>
    
    @Query("SELECT * FROM loadouts ORDER BY updatedAt DESC")
    fun getAllLoadoutsFlow(): Flow<List<LoadoutEntity>>
    
    @Query("SELECT * FROM loadouts WHERE isEquipped = 1 AND characterId = :characterId LIMIT 1")
    suspend fun getEquippedLoadout(characterId: String): LoadoutEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoadout(loadout: LoadoutEntity): Long
    
    @Update
    suspend fun updateLoadout(loadout: LoadoutEntity)
    
    @Delete
    suspend fun deleteLoadout(loadout: LoadoutEntity)
    
    @Query("DELETE FROM loadouts WHERE id = :loadoutId")
    suspend fun deleteLoadoutById(loadoutId: String)
    
    @Query("DELETE FROM loadouts WHERE characterId = :characterId")
    suspend fun deleteLoadoutsForCharacter(characterId: String)
    
    @Query("UPDATE loadouts SET isEquipped = 0 WHERE characterId = :characterId")
    suspend fun unequipAllLoadoutsForCharacter(characterId: String)
}
