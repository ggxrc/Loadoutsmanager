package com.ads.loadoutsmanager.data.database.dao

import androidx.room.*
import com.ads.loadoutsmanager.data.database.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for items
 */
@Dao
interface ItemDao {
    
    @Query("SELECT * FROM items WHERE itemInstanceId = :itemId")
    suspend fun getItem(itemId: String): ItemEntity?
    
    @Query("SELECT * FROM items WHERE itemInstanceId IN (:itemIds)")
    suspend fun getItems(itemIds: List<String>): List<ItemEntity>
    
    @Query("SELECT * FROM items")
    fun getAllItemsFlow(): Flow<List<ItemEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItemEntity>)
    
    @Update
    suspend fun updateItem(item: ItemEntity)
    
    @Delete
    suspend fun deleteItem(item: ItemEntity)
    
    @Query("DELETE FROM items WHERE itemInstanceId = :itemId")
    suspend fun deleteItemById(itemId: String)
    
    @Query("DELETE FROM items")
    suspend fun deleteAllItems()
}
