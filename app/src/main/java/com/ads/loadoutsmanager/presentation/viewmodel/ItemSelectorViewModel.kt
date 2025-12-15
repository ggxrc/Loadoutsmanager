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
    private val characterId: String
) : ViewModel() {

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

    sealed class ItemSelectorUiState {
        object Loading : ItemSelectorUiState()
        object Success : ItemSelectorUiState()
        data class Error(val message: String) : ItemSelectorUiState()
    }

    init {
        loadItems()
    }

    /**
     * Load items from all sources (equipped, inventory, vault)
     */
    fun loadItems() {
        viewModelScope.launch {
            _uiState.value = ItemSelectorUiState.Loading

            try {
                // Load equipped items
                loadoutRepository.getEquippedItemsForCharacter(characterId).fold(
                    onSuccess = { items ->
                        android.util.Log.d("ItemSelectorVM", "✅ Loaded ${items.size} equipped items")
                        _equippedItems.value = items
                    },
                    onFailure = { error ->
                        android.util.Log.e("ItemSelectorVM", "❌ Failed to load equipped items: ${error.message}")
                    }
                )

                // Load inventory items
                loadoutRepository.getInventoryItemsForCharacter(characterId).fold(
                    onSuccess = { items ->
                        android.util.Log.d("ItemSelectorVM", "✅ Loaded ${items.size} inventory items")
                        _inventoryItems.value = items
                    },
                    onFailure = { error ->
                        android.util.Log.e("ItemSelectorVM", "❌ Failed to load inventory: ${error.message}")
                    }
                )

                // Load vault items
                loadoutRepository.getVaultItems().fold(
                    onSuccess = { items ->
                        android.util.Log.d("ItemSelectorVM", "✅ Loaded ${items.size} vault items")
                        _vaultItems.value = items
                    },
                    onFailure = { error ->
                        android.util.Log.e("ItemSelectorVM", "❌ Failed to load vault: ${error.message}")
                    }
                )

                _uiState.value = ItemSelectorUiState.Success
            } catch (e: Exception) {
                android.util.Log.e("ItemSelectorVM", "❌ Exception loading items", e)
                _uiState.value = ItemSelectorUiState.Error(e.message ?: "Failed to load items")
            }
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

