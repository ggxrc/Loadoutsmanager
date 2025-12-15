package com.ads.loadoutsmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ads.loadoutsmanager.data.model.DestinyItem
import com.ads.loadoutsmanager.data.repository.LoadoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing item selection and inventory access
 */
class ItemSelectorViewModel(
    private val loadoutRepository: LoadoutRepository,
    initialCharacterId: String
) : ViewModel() {

    // Character ID can be updated when switching characters
    private var currentCharacterId: String = initialCharacterId

    private val _equippedItems = MutableStateFlow<List<DestinyItem>>(emptyList())
    val equippedItems: StateFlow<List<DestinyItem>> = _equippedItems.asStateFlow()

    private val _inventoryItems = MutableStateFlow<List<DestinyItem>>(emptyList())
    val inventoryItems: StateFlow<List<DestinyItem>> = _inventoryItems.asStateFlow()

    private val _vaultItems = MutableStateFlow<List<DestinyItem>>(emptyList())
    val vaultItems: StateFlow<List<DestinyItem>> = _vaultItems.asStateFlow()

    private val _selectedItems = MutableStateFlow<List<DestinyItem>>(emptyList())
    val selectedItems: StateFlow<List<DestinyItem>> = _selectedItems.asStateFlow()

    private val _uiState = MutableStateFlow<ItemSelectorUiState>(ItemSelectorUiState.Loading)
    val uiState: StateFlow<ItemSelectorUiState> = _uiState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    // Vault pagination
    private val _vaultPage = MutableStateFlow(0)
    val vaultPage: StateFlow<Int> = _vaultPage.asStateFlow()
    
    private val _vaultHasMore = MutableStateFlow(false)
    val vaultHasMore: StateFlow<Boolean> = _vaultHasMore.asStateFlow()
    
    private val _allVaultItems = MutableStateFlow<List<DestinyItem>>(emptyList())
    
    companion object {
        const val VAULT_PAGE_SIZE = 15
    }

    sealed class ItemSelectorUiState {
        object Loading : ItemSelectorUiState()
        object Success : ItemSelectorUiState()
        data class Error(val message: String) : ItemSelectorUiState()
    }

    /**
     * Update character ID and reload items
     */
    fun updateCharacterId(newCharacterId: String) {
        if (currentCharacterId != newCharacterId) {
            android.util.Log.d("ItemSelectorVM", "🔄 Switching character from $currentCharacterId to $newCharacterId")
            currentCharacterId = newCharacterId
            loadItems()
        }
    }

    /**
     * Load items from all sources (equipped, inventory, vault)
     */
    fun loadItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _uiState.value = ItemSelectorUiState.Loading

            try {
                android.util.Log.d("ItemSelectorVM", "📡 Loading items for character $currentCharacterId...")

                // Load equipped items
                loadoutRepository.getEquippedItemsForCharacter(currentCharacterId).fold(
                    onSuccess = { items ->
                        android.util.Log.d("ItemSelectorVM", "✅ Loaded ${items.size} equipped items")
                        _equippedItems.value = items
                    },
                    onFailure = { error ->
                        android.util.Log.e("ItemSelectorVM", "❌ Failed to load equipped items: ${error.message}")
                        _equippedItems.value = emptyList()
                    }
                )

                // Load inventory items
                loadoutRepository.getInventoryItemsForCharacter(currentCharacterId).fold(
                    onSuccess = { items ->
                        android.util.Log.d("ItemSelectorVM", "✅ Loaded ${items.size} inventory items")
                        _inventoryItems.value = items
                    },
                    onFailure = { error ->
                        android.util.Log.e("ItemSelectorVM", "❌ Failed to load inventory: ${error.message}")
                        _inventoryItems.value = emptyList()
                    }
                )

                // Load vault items with pagination from local database
                loadoutRepository.getVaultItemsFromDatabase().fold(
                    onSuccess = { items ->
                        android.util.Log.d("ItemSelectorVM", "✅ Loaded ${items.size} vault items from DB")
                        _allVaultItems.value = items
                        _vaultPage.value = 0
                        updateVaultItemsForPage()
                    },
                    onFailure = { error ->
                        android.util.Log.e("ItemSelectorVM", "❌ Failed to load vault from DB: ${error.message}")
                        // Fallback to empty list
                        _allVaultItems.value = emptyList()
                        _vaultItems.value = emptyList()
                    }
                )

                _uiState.value = ItemSelectorUiState.Success
                android.util.Log.d("ItemSelectorVM", "✅ All items loaded successfully")
            } catch (e: Exception) {
                android.util.Log.e("ItemSelectorVM", "❌ Exception loading items", e)
                _uiState.value = ItemSelectorUiState.Error(e.message ?: "Failed to load items")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Refresh all items (for pull-to-refresh)
     */
    fun refreshItems() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadItems()
            _isRefreshing.value = false
        }
    }
    
    /**
     * Sync vault from API to local database
     * Fetches all vault items from Bungie API and stores them locally
     */
    fun syncVaultFromAPI() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _uiState.value = ItemSelectorUiState.Loading
            
            android.util.Log.d("ItemSelectorVM", "🔄 Syncing vault from API...")
            
            loadoutRepository.syncVaultToDatabase().fold(
                onSuccess = { count ->
                    android.util.Log.d("ItemSelectorVM", "✅ Synced $count vault items")
                    // Reload vault items from database
                    loadoutRepository.getVaultItemsFromDatabase().fold(
                        onSuccess = { items ->
                            _allVaultItems.value = items
                            _vaultPage.value = 0
                            updateVaultItemsForPage()
                            _uiState.value = ItemSelectorUiState.Success
                        },
                        onFailure = { error ->
                            android.util.Log.e("ItemSelectorVM", "❌ Failed to reload vault after sync: ${error.message}")
                            _uiState.value = ItemSelectorUiState.Error(error.message ?: "Failed to reload vault")
                        }
                    )
                },
                onFailure = { error ->
                    android.util.Log.e("ItemSelectorVM", "❌ Failed to sync vault: ${error.message}")
                    _uiState.value = ItemSelectorUiState.Error(error.message ?: "Failed to sync vault")
                }
            )
            
            _isRefreshing.value = false
        }
    }
    
    /**
     * Load only vault items (for vault tab refresh button)
     * @deprecated Use syncVaultFromAPI() instead
     */
    fun loadVaultItems() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _vaultPage.value = 0 // Reset page
            
            loadoutRepository.getVaultItemsFromDatabase().fold(
                onSuccess = { items ->
                    android.util.Log.d("ItemSelectorVM", "✅ Reloaded ${items.size} vault items from DB")
                    _allVaultItems.value = items
                    updateVaultItemsForPage()
                },
                onFailure = { error ->
                    android.util.Log.e("ItemSelectorVM", "❌ Failed to reload vault: ${error.message}")
                }
            )
            
            _isRefreshing.value = false
        }
    }
    
    /**
     * Load next page of vault items
     */
    fun loadNextVaultPage() {
        if (_vaultHasMore.value) {
            _vaultPage.value += 1
            updateVaultItemsForPage()
        }
    }
    
    /**
     * Load previous page of vault items
     */
    fun loadPreviousVaultPage() {
        if (_vaultPage.value > 0) {
            _vaultPage.value -= 1
            updateVaultItemsForPage()
        }
    }
    
    /**
     * Update displayed vault items based on current page
     */
    private fun updateVaultItemsForPage() {
        val allItems = _allVaultItems.value
        val startIndex = _vaultPage.value * VAULT_PAGE_SIZE
        val endIndex = minOf(startIndex + VAULT_PAGE_SIZE, allItems.size)
        
        if (startIndex < allItems.size) {
            _vaultItems.value = allItems.subList(startIndex, endIndex)
            _vaultHasMore.value = endIndex < allItems.size
            android.util.Log.d("ItemSelectorVM", "📖 Page ${_vaultPage.value}: showing items $startIndex-$endIndex of ${allItems.size}")
        } else {
            _vaultItems.value = emptyList()
            _vaultHasMore.value = false
        }
    }

    /**
     * Toggle item selection
     */
    fun toggleItemSelection(item: DestinyItem) {
        val currentSelection = _selectedItems.value.toMutableList()

        val existingIndex = currentSelection.indexOfFirst {
            it.itemInstanceId == item.itemInstanceId
        }

        if (existingIndex >= 0) {
            // Item already selected, remove it
            currentSelection.removeAt(existingIndex)
            android.util.Log.d("ItemSelectorVM", "➖ Deselected item: ${item.itemInstanceId}")
        } else {
            // Item not selected, add it
            currentSelection.add(item)
            android.util.Log.d("ItemSelectorVM", "➕ Selected item: ${item.itemInstanceId}")
        }

        _selectedItems.value = currentSelection
    }

    /**
     * Clear all selections
     */
    fun clearSelection() {
        _selectedItems.value = emptyList()
        android.util.Log.d("ItemSelectorVM", "🗑️ Cleared selection")
    }

    /**
     * Get selected items
     */
    fun getSelectedItems(): List<DestinyItem> {
        return _selectedItems.value
    }

    class Factory(
        private val loadoutRepository: LoadoutRepository,
        private val characterId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ItemSelectorViewModel::class.java)) {
                return ItemSelectorViewModel(loadoutRepository, characterId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

