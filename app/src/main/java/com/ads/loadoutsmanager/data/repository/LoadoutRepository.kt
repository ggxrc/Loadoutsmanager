package com.ads.loadoutsmanager.data.repository

import com.ads.loadoutsmanager.data.api.BungieApiService
import com.ads.loadoutsmanager.data.api.EquipItemsRequest
import com.ads.loadoutsmanager.data.api.TransferItemRequest
import com.ads.loadoutsmanager.data.model.DestinyLoadout
import com.ads.loadoutsmanager.data.model.DestinyItem
import com.ads.loadoutsmanager.data.model.ItemLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for managing Destiny 2 loadouts
 * Handles CRUD operations and communication with Bungie API
 */
class LoadoutRepository(
    private val bungieApiService: BungieApiService,
    private val membershipType: Int,
    private val membershipId: String
) {
    
    // In-memory storage for loadouts
    // TODO: Replace with Room database for persistent storage
    private val loadouts = mutableListOf<DestinyLoadout>()
    private val loadoutsLock = Any()
    
    /**
     * Get all loadouts for a character
     */
    suspend fun getLoadouts(characterId: String): List<DestinyLoadout> = withContext(Dispatchers.IO) {
        synchronized(loadoutsLock) {
            loadouts.filter { it.characterId == characterId }
        }
    }
    
    /**
     * Get a specific loadout by ID
     */
    suspend fun getLoadout(loadoutId: String): DestinyLoadout? = withContext(Dispatchers.IO) {
        synchronized(loadoutsLock) {
            loadouts.find { it.id == loadoutId }
        }
    }
    
    /**
     * Create a new loadout
     */
    suspend fun createLoadout(loadout: DestinyLoadout): Result<DestinyLoadout> = withContext(Dispatchers.IO) {
        try {
            synchronized(loadoutsLock) {
                loadouts.add(loadout)
            }
            Result.success(loadout)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update an existing loadout
     */
    suspend fun updateLoadout(loadout: DestinyLoadout): Result<DestinyLoadout> = withContext(Dispatchers.IO) {
        try {
            synchronized(loadoutsLock) {
                val index = loadouts.indexOfFirst { it.id == loadout.id }
                if (index != -1) {
                    loadouts[index] = loadout.copy(updatedAt = System.currentTimeMillis())
                    Result.success(loadouts[index])
                } else {
                    Result.failure(Exception("Loadout not found"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete a loadout
     */
    suspend fun deleteLoadout(loadoutId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val removed = synchronized(loadoutsLock) {
                loadouts.removeIf { it.id == loadoutId }
            }
            Result.success(removed)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Equip a loadout - equips all items in the loadout
     */
    suspend fun equipLoadout(loadout: DestinyLoadout): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // First, transfer items from vault to character if needed
            val itemsToTransfer = loadout.equipment.filter { it.location == ItemLocation.VAULT }
            for (item in itemsToTransfer) {
                val transferRequest = TransferItemRequest(
                    itemReferenceHash = item.itemHash,
                    stackSize = 1,
                    transferToVault = false,
                    itemId = item.itemInstanceId,
                    characterId = loadout.characterId,
                    membershipType = membershipType
                )
                val response = bungieApiService.transferItem(transferRequest)
                if (!response.isSuccessful || response.body()?.ErrorCode != 1) {
                    return@withContext Result.failure(
                        Exception("Failed to transfer item: ${response.body()?.ErrorStatus}")
                    )
                }
            }
            
            // Then equip all items
            val itemIds = loadout.equipment.map { it.itemInstanceId }
            val equipRequest = EquipItemsRequest(
                itemIds = itemIds,
                characterId = loadout.characterId,
                membershipType = membershipType
            )
            
            val response = bungieApiService.equipItems(equipRequest)
            if (response.isSuccessful && response.body()?.ErrorCode == 1) {
                // Update loadout status
                updateLoadout(loadout.copy(isEquipped = true))
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to equip loadout: ${response.body()?.ErrorStatus}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Unequip a loadout and store items in vault
     */
    suspend fun unequipLoadoutToVault(loadout: DestinyLoadout): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Transfer all equipped items to vault
            for (item in loadout.equipment) {
                val transferRequest = TransferItemRequest(
                    itemReferenceHash = item.itemHash,
                    stackSize = 1,
                    transferToVault = true,
                    itemId = item.itemInstanceId,
                    characterId = loadout.characterId,
                    membershipType = membershipType
                )
                val response = bungieApiService.transferItem(transferRequest)
                if (!response.isSuccessful || response.body()?.ErrorCode != 1) {
                    return@withContext Result.failure(
                        Exception("Failed to transfer item to vault: ${response.body()?.ErrorStatus}")
                    )
                }
            }
            
            // Update loadout status
            val updatedItems = loadout.equipment.map { it.copy(location = ItemLocation.VAULT) }
            updateLoadout(loadout.copy(equipment = updatedItems, isEquipped = false))
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get currently equipped items for a character
     * Note: Response parsing would need to be implemented based on actual API response structure
     */
    suspend fun getEquippedItems(characterId: String): Result<List<DestinyItem>> = withContext(Dispatchers.IO) {
        try {
            val response = bungieApiService.getCharacter(
                membershipType = membershipType,
                destinyMembershipId = membershipId,
                characterId = characterId,
                components = "205" // CharacterEquipment component
            )
            
            if (response.isSuccessful && response.body()?.ErrorCode == 1) {
                // TODO: Parse equipped items from response.body()?.Response?.equipment?.data
                // The actual parsing would depend on the Bungie API response structure
                // For now, return empty list as placeholder
                // Future implementation should deserialize the equipment data into DestinyItem objects
                val items = listOf<DestinyItem>()
                Result.success(items)
            } else {
                Result.failure(Exception("Failed to get equipped items: ${response.body()?.ErrorStatus}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
