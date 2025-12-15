package com.ads.loadoutsmanager.data.repository

import com.ads.loadoutsmanager.data.api.BungieApiService
import com.ads.loadoutsmanager.data.api.EquipItemsRequest
import com.ads.loadoutsmanager.data.api.TransferItemRequest
import com.ads.loadoutsmanager.data.database.LoadoutsDatabase
import com.ads.loadoutsmanager.data.database.toEntity
import com.ads.loadoutsmanager.data.database.toDomain
import com.ads.loadoutsmanager.data.model.DestinyLoadout
import com.ads.loadoutsmanager.data.model.DestinyItem
import com.ads.loadoutsmanager.data.model.ItemLocation
import com.ads.loadoutsmanager.data.model.toDestinyCharacter
import com.ads.loadoutsmanager.data.model.toDestinyItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for managing Destiny 2 loadouts
 * Handles CRUD operations and communication with Bungie API
 * Uses Room database for persistent local storage
 * Implements inventory checking flow: target character -> other characters -> vault
 */
class LoadoutRepository(
    private val bungieApiService: BungieApiService,
    private val database: LoadoutsDatabase,
    private val membershipType: Int,
    private val membershipId: String
) {
    
    private val loadoutDao = database.loadoutDao()
    private val itemDao = database.itemDao()
    
    /**
     * Get all loadouts for a character from local database
     */
    suspend fun getLoadouts(characterId: String): List<DestinyLoadout> = withContext(Dispatchers.IO) {
        val loadoutEntities = loadoutDao.getLoadoutsForCharacter(characterId)
        loadoutEntities.map { it.toDomain(itemDao) }
    }
    
    /**
     * Get a specific loadout by ID from local database
     */
    suspend fun getLoadout(loadoutId: String): DestinyLoadout? = withContext(Dispatchers.IO) {
        loadoutDao.getLoadout(loadoutId)?.toDomain(itemDao)
    }
    
    /**
     * Create a new loadout and save to local database
     */
    suspend fun createLoadout(loadout: DestinyLoadout): Result<DestinyLoadout> = withContext(Dispatchers.IO) {
        try {
            // Save items to database
            val itemEntities = loadout.equipment.map { it.toEntity() }
            itemDao.insertItems(itemEntities)
            
            // Save loadout to database
            loadoutDao.insertLoadout(loadout.toEntity())
            Result.success(loadout)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update an existing loadout in local database
     */
    suspend fun updateLoadout(loadout: DestinyLoadout): Result<DestinyLoadout> = withContext(Dispatchers.IO) {
        try {
            val updatedLoadout = loadout.copy(updatedAt = System.currentTimeMillis())
            
            // Update items in database
            val itemEntities = updatedLoadout.equipment.map { it.toEntity() }
            itemDao.insertItems(itemEntities)
            
            // Update loadout in database
            loadoutDao.updateLoadout(updatedLoadout.toEntity())
            Result.success(updatedLoadout)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete a loadout from local database
     */
    suspend fun deleteLoadout(loadoutId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            loadoutDao.deleteLoadoutById(loadoutId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Equip a loadout - implements inventory checking flow:
     * 1. Check target character inventory
     * 2. Check other characters' inventories
     * 3. Check vault (last resort)
     * Transfer items as needed and equip all items
     */
    suspend fun equipLoadout(
        loadout: DestinyLoadout,
        allCharacterIds: List<String>
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Unequip any currently equipped loadout for this character
            loadoutDao.unequipAllLoadoutsForCharacter(loadout.characterId)
            
            // Transfer items to target character following the inventory flow
            for (item in loadout.equipment) {
                val itemLocation = findItemLocation(item, loadout.characterId, allCharacterIds)
                
                when {
                    // Item is already on target character - no transfer needed
                    itemLocation == ItemLocation.EQUIPPED || 
                    itemLocation == ItemLocation.INVENTORY -> {
                        // Item is already on the target character
                        continue
                    }
                    
                    // Item is on another character - transfer from that character
                    itemLocation == ItemLocation.INVENTORY -> {
                        // First transfer to vault, then to target character
                        transferItemBetweenCharacters(item, loadout.characterId)
                    }
                    
                    // Item is in vault - transfer directly to target character
                    itemLocation == ItemLocation.VAULT -> {
                        val transferRequest = TransferItemRequest(
                            itemReferenceHash = item.itemHash,
                            stackSize = 1,
                            transferToVault = false,
                            itemId = item.itemInstanceId,
                            characterId = loadout.characterId,
                            membershipType = membershipType
                        )
                        val response = bungieApiService.transferItem(transferRequest)
                        if (response.isError) {
                            return@withContext Result.failure(
                                Exception("Failed to transfer item from vault: ${response.ErrorStatus}")
                            )
                        }
                    }
                }
            }
            
            // Equip all items
            val itemIds = loadout.equipment.map { it.itemInstanceId }
            val equipRequest = EquipItemsRequest(
                itemIds = itemIds,
                characterId = loadout.characterId,
                membershipType = membershipType
            )
            
            val response = bungieApiService.equipItems(equipRequest)
            if (response.isSuccess) {
                // Update loadout status in database
                val updatedLoadout = loadout.copy(isEquipped = true)
                updateLoadout(updatedLoadout)
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to equip loadout: ${response.ErrorStatus}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Find item location following the priority:
     * 1. Target character
     * 2. Other characters
     * 3. Vault
     */
    private suspend fun findItemLocation(
        item: DestinyItem,
        targetCharacterId: String,
        allCharacterIds: List<String>
    ): ItemLocation {
        // This is a simplified version - in production, you would query the API
        // to get actual item locations across all characters and vault
        // For now, we use the item's current location from the loadout
        return item.location
    }
    
    /**
     * Transfer item between characters (via vault)
     * Items cannot be transferred directly between characters - must go through vault
     */
    private suspend fun transferItemBetweenCharacters(
        item: DestinyItem,
        targetCharacterId: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // TODO: Track source character ID properly
            // For now, this is a placeholder that would need to be enhanced
            // to properly track which character currently has the item
            
            // Step 1: Transfer to vault from source character
            // NOTE: This requires knowing the source character ID
            // In a real implementation, you would query the API to find
            // where the item is currently located
            val toVaultRequest = TransferItemRequest(
                itemReferenceHash = item.itemHash,
                stackSize = 1,
                transferToVault = true,
                itemId = item.itemInstanceId,
                characterId = "", // TODO: Get actual source character ID from API
                membershipType = membershipType
            )
            val toVaultResponse = bungieApiService.transferItem(toVaultRequest)
            if (toVaultResponse.isError) {
                return@withContext Result.failure(
                    Exception("Failed to transfer item to vault: ${toVaultResponse.ErrorStatus}")
                )
            }
            
            // Step 2: Transfer from vault to target character
            val toCharacterRequest = TransferItemRequest(
                itemReferenceHash = item.itemHash,
                stackSize = 1,
                transferToVault = false,
                itemId = item.itemInstanceId,
                characterId = targetCharacterId,
                membershipType = membershipType
            )
            val toCharacterResponse = bungieApiService.transferItem(toCharacterRequest)
            if (toCharacterResponse.isSuccess) {
                return@withContext Result.success(true)
            } else {
                return@withContext Result.failure(
                    Exception("Failed to transfer item to character: ${toCharacterResponse.ErrorStatus}")
                )
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
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
                if (response.isError) {
                    return@withContext Result.failure(
                        Exception("Failed to transfer item: ${response.ErrorStatus}")
                    )
                }
            }
            
            // Update loadout status
            val updatedItems = loadout.equipment.map { it.copy(location = ItemLocation.VAULT) }
            val updatedLoadout = loadout.copy(equipment = updatedItems, isEquipped = false)
            updateLoadout(updatedLoadout)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get currently equipped items for a character (always fetched from API, not cached)
     * Note: Response parsing would need to be implemented based on actual API response structure
     */
    suspend fun getEquippedItems(characterId: String): Result<List<DestinyItem>> = withContext(Dispatchers.IO) {
        try {
            val response = bungieApiService.getCharacter(
                membershipType = membershipType,
                destinyMembershipId = membershipId,
                characterId = characterId,
                components = "205,300,305" // CharacterEquipment, ItemInstances, ItemSockets
            )
            
            if (response.isSuccess) {
                // TODO: Parse equipped items from response.Response?.equipment?.data
                // The actual parsing would depend on the Bungie API response structure
                // For now, return empty list as placeholder
                // Future implementation should deserialize the equipment data into DestinyItem objects
                val items = listOf<DestinyItem>()
                Result.success(items)
            } else {
                Result.failure(Exception("Failed to get equipped items: ${response.ErrorStatus}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get all characters from Bungie API
     * Component 200: Characters
     */
    suspend fun getCharacters(): Result<List<com.ads.loadoutsmanager.data.model.DestinyCharacter>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("LoadoutRepository", "📡 Fetching characters from API...")

            val response = bungieApiService.getProfileCharacters(
                membershipType = membershipType,
                destinyMembershipId = membershipId,
                components = "200"
            )

            if (response.isSuccess && response.Response != null) {
                val charactersData = response.Response.characters?.data

                if (charactersData != null) {
                    val characters = charactersData.values.map { it.toDestinyCharacter() }
                    android.util.Log.d("LoadoutRepository", "✅ Loaded ${characters.size} characters")
                    return@withContext Result.success(characters)
                } else {
                    android.util.Log.w("LoadoutRepository", "⚠️ No character data in response")
                    return@withContext Result.failure(Exception("No character data available"))
                }
            } else {
                android.util.Log.e("LoadoutRepository", "❌ API error: ${response.ErrorStatus}")
                return@withContext Result.failure(
                    Exception("Failed to get characters: ${response.ErrorStatus}")
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("LoadoutRepository", "❌ Exception getting characters", e)
            return@withContext Result.failure(e)
        }
    }

    /**
     * Get equipped items for a character
     * Component 205: CharacterEquipment
     * Component 300: ItemInstances
     */
    suspend fun getEquippedItemsForCharacter(characterId: String): Result<List<DestinyItem>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("LoadoutRepository", "📡 Fetching equipped items for character $characterId...")

            val response = bungieApiService.getProfileEquipment(
                membershipType = membershipType,
                destinyMembershipId = membershipId,
                components = "205,300"
            )

            if (response.isSuccess && response.Response != null) {
                val equipmentData = response.Response.characterEquipment?.data?.get(characterId)

                if (equipmentData != null && equipmentData.items != null) {
                    val items = equipmentData.items.mapNotNull { item ->
                        // Only include weapons and armor (exclude subclass, ghost, etc for now)
                        if (isWeaponOrArmor(item.bucketHash)) {
                            item.toDestinyItem(ItemLocation.EQUIPPED)
                        } else null
                    }
                    android.util.Log.d("LoadoutRepository", "✅ Loaded ${items.size} equipped items")
                    return@withContext Result.success(items)
                } else {
                    android.util.Log.w("LoadoutRepository", "⚠️ No equipment data for character")
                    return@withContext Result.success(emptyList())
                }
            } else {
                android.util.Log.e("LoadoutRepository", "❌ API error: ${response.ErrorStatus}")
                return@withContext Result.failure(
                    Exception("Failed to get equipment: ${response.ErrorStatus}")
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("LoadoutRepository", "❌ Exception getting equipment", e)
            return@withContext Result.failure(e)
        }
    }

    /**
     * Get inventory items for a character
     * Component 201: CharacterInventories
     * Component 300: ItemInstances
     */
    suspend fun getInventoryItemsForCharacter(characterId: String): Result<List<DestinyItem>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("LoadoutRepository", "📡 Fetching inventory for character $characterId...")

            val response = bungieApiService.getProfileInventories(
                membershipType = membershipType,
                destinyMembershipId = membershipId,
                components = "201,300"
            )

            if (response.isSuccess && response.Response != null) {
                val inventoryData = response.Response.characterInventories?.data?.get(characterId)

                if (inventoryData != null && inventoryData.items != null) {
                    val items = inventoryData.items.mapNotNull { item ->
                        if (isWeaponOrArmor(item.bucketHash)) {
                            item.toDestinyItem(ItemLocation.INVENTORY)
                        } else null
                    }
                    android.util.Log.d("LoadoutRepository", "✅ Loaded ${items.size} inventory items")
                    return@withContext Result.success(items)
                } else {
                    android.util.Log.w("LoadoutRepository", "⚠️ No inventory data for character")
                    return@withContext Result.success(emptyList())
                }
            } else {
                android.util.Log.e("LoadoutRepository", "❌ API error: ${response.ErrorStatus}")
                return@withContext Result.failure(
                    Exception("Failed to get inventory: ${response.ErrorStatus}")
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("LoadoutRepository", "❌ Exception getting inventory", e)
            return@withContext Result.failure(e)
        }
    }

    /**
     * Get vault items
     * Component 102: ProfileInventory (Vault)
     * Component 300: ItemInstances
     */
    suspend fun getVaultItems(): Result<List<DestinyItem>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("LoadoutRepository", "📡 Fetching vault items...")

            val response = bungieApiService.getProfileInventories(
                membershipType = membershipType,
                destinyMembershipId = membershipId,
                components = "102,300"
            )
            
            if (response.isSuccess && response.Response != null) {
                val vaultData = response.Response.profileInventory?.data

                if (vaultData != null && vaultData.items != null) {
                    val items = vaultData.items.mapNotNull { item ->
                        if (isWeaponOrArmor(item.bucketHash)) {
                            item.toDestinyItem(ItemLocation.VAULT)
                        } else null
                    }
                    android.util.Log.d("LoadoutRepository", "✅ Loaded ${items.size} vault items")
                    return@withContext Result.success(items)
                } else {
                    android.util.Log.w("LoadoutRepository", "⚠️ No vault data")
                    return@withContext Result.success(emptyList())
                }
            } else {
                android.util.Log.e("LoadoutRepository", "❌ API error: ${response.ErrorStatus}")
                return@withContext Result.failure(
                    Exception("Failed to get vault: ${response.ErrorStatus}")
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("LoadoutRepository", "❌ Exception getting vault", e)
            return@withContext Result.failure(e)
        }
    }

    /**
     * Helper function to check if bucket is weapon or armor
     * Bucket hashes from Bungie API documentation:
     * - Kinetic Weapon: 1498876634
     * - Energy Weapon: 2465295065
     * - Power Weapon: 953998645
     * - Helmet: 3448274439
     * - Gauntlets: 3551918588
     * - Chest: 14239492
     * - Legs: 20886954
     * - Class Item: 1585787867
     */
    private fun isWeaponOrArmor(bucketHash: Long): Boolean {
        return bucketHash in setOf(
            1498876634L, // Kinetic
            2465295065L, // Energy
            953998645L,  // Power
            3448274439L, // Helmet
            3551918588L, // Gauntlets
            14239492L,   // Chest
            20886954L,   // Legs
            1585787867L  // Class Item
        )
    }
}
