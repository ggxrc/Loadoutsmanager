package com.ads.loadoutsmanager.data.repository

import com.ads.loadoutsmanager.data.model.DestinyItem

/**
 * Service responsible for searching and retrieving equipment from inventory and vault
 * Search order: Target character inventory -> Other characters inventory -> Vault
 */
class EquipmentSearchService {
    
    /**
     * Searches for an item across all available locations
     * @param itemHash The hash identifier of the item to find
     * @param targetCharacterId The character that needs the item
     * @param allCharacterInventories Map of character IDs to their inventories
     * @param vaultInventory Items currently in the vault
     * @return The found item with its location, or null if not found
     */
    fun findItem(
        itemHash: Long,
        targetCharacterId: String,
        allCharacterInventories: Map<String, List<DestinyItem>>,
        vaultInventory: List<DestinyItem>
    ): ItemSearchResult? {
        // 1. Check target character's inventory first
        allCharacterInventories[targetCharacterId]?.let { targetInventory ->
            val item = targetInventory.firstOrNull { it.itemHash == itemHash }
            if (item != null) {
                return ItemSearchResult(
                    item = item,
                    sourceLocation = SearchLocation.TARGET_CHARACTER,
                    sourceCharacterId = targetCharacterId,
                    requiresTransfer = false
                )
            }
        }
        
        // 2. Check other characters' inventories
        allCharacterInventories.forEach { (characterId, inventory) ->
            if (characterId != targetCharacterId) {
                val item = inventory.firstOrNull { it.itemHash == itemHash }
                if (item != null) {
                    return ItemSearchResult(
                        item = item,
                        sourceLocation = SearchLocation.OTHER_CHARACTER,
                        sourceCharacterId = characterId,
                        requiresTransfer = true
                    )
                }
            }
        }
        
        // 3. Finally, check the vault
        val item = vaultInventory.firstOrNull { it.itemHash == itemHash }
        if (item != null) {
            return ItemSearchResult(
                item = item,
                sourceLocation = SearchLocation.VAULT,
                sourceCharacterId = null,
                requiresTransfer = true
            )
        }
        
        return null
    }
    
    /**
     * Searches for multiple items at once
     * @return Map of item hashes to their search results
     */
    fun findMultipleItems(
        itemHashes: List<Long>,
        targetCharacterId: String,
        allCharacterInventories: Map<String, List<DestinyItem>>,
        vaultInventory: List<DestinyItem>
    ): Map<Long, ItemSearchResult?> {
        return itemHashes.associateWith { hash ->
            findItem(hash, targetCharacterId, allCharacterInventories, vaultInventory)
        }
    }
}

data class ItemSearchResult(
    val item: DestinyItem,
    val sourceLocation: SearchLocation,
    val sourceCharacterId: String?,
    val requiresTransfer: Boolean
)

enum class SearchLocation {
    TARGET_CHARACTER,
    OTHER_CHARACTER,
    VAULT,
    NOT_FOUND
}
