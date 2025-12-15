package com.ads.loadoutsmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ads.loadoutsmanager.data.api.ManifestService
import com.ads.loadoutsmanager.data.model.DamageType
import com.ads.loadoutsmanager.data.model.ItemDetails
import com.ads.loadoutsmanager.data.model.TierType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for fetching and managing item details from Bungie Manifest
 */
class ItemDetailViewModel(
    private val manifestService: ManifestService,
    private val itemHash: Long
) : ViewModel() {

    private val _itemDetails = MutableStateFlow<ItemDetails?>(null)
    val itemDetails: StateFlow<ItemDetails?> = _itemDetails.asStateFlow()

    private val _uiState = MutableStateFlow<ItemDetailUiState>(ItemDetailUiState.Loading)
    val uiState: StateFlow<ItemDetailUiState> = _uiState.asStateFlow()

    sealed class ItemDetailUiState {
        object Loading : ItemDetailUiState()
        object Success : ItemDetailUiState()
        data class Error(val message: String) : ItemDetailUiState()
    }

    init {
        loadItemDetails()
    }

    /**
     * Load item definition from manifest
     */
    fun loadItemDetails() {
        viewModelScope.launch {
            _uiState.value = ItemDetailUiState.Loading

            try {
                val response = manifestService.getItemDefinition(itemHash)
                
                if (response.isSuccess && response.Response != null) {
                    val definition = response.Response
                    
                    // Convert API response to ItemDetails model
                    val itemDetails = ItemDetails(
                        itemInstanceId = "",
                        itemHash = itemHash,
                        name = definition.displayProperties?.name ?: "Unknown Item",
                        description = definition.displayProperties?.description ?: "",
                        icon = definition.displayProperties?.icon ?: "",
                        screenshot = null,
                        damageType = DamageType.KINETIC,
                        power = 0,
                        tierType = TierType.COMMON,
                        itemType = definition.itemTypeDisplayName ?: "",
                        itemSubType = definition.itemTypeAndTierDisplayName ?: "",
                        stats = emptyMap(),
                        perks = null,
                        sockets = null
                    )
                    
                    _itemDetails.value = itemDetails
                    _uiState.value = ItemDetailUiState.Success
                    
                    android.util.Log.d("ItemDetailVM", "✅ Loaded details for item: ${itemDetails.name}")
                } else {
                    android.util.Log.e("ItemDetailVM", "❌ Failed to load item definition: ${response.ErrorStatus}")
                    _uiState.value = ItemDetailUiState.Error(
                        response.ErrorStatus ?: "Failed to load item details"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("ItemDetailVM", "❌ Exception loading item details", e)
                _uiState.value = ItemDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    class Factory(
        private val manifestService: ManifestService,
        private val itemHash: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ItemDetailViewModel::class.java)) {
                return ItemDetailViewModel(manifestService, itemHash) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
