package com.ads.loadoutsmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ads.loadoutsmanager.data.repository.LoadoutRepository
import com.ads.loadoutsmanager.data.model.DestinyCharacter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing characters and inventory
 */
class CharacterViewModel(
    private val loadoutRepository: LoadoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharacterUiState>(CharacterUiState.Loading)
    val uiState: StateFlow<CharacterUiState> = _uiState

    sealed class CharacterUiState {
        object Loading : CharacterUiState()
        data class Success(val message: String) : CharacterUiState()
        data class Error(val message: String) : CharacterUiState()
    }

    /**
     * Test accessing characters
     */
    fun testCharacterAccess(membershipType: Int, membershipId: String, characterId: String) {
        viewModelScope.launch {
            _uiState.value = CharacterUiState.Loading

            val result = loadoutRepository.getEquippedItems(characterId)

            result.fold(
                onSuccess = { items ->
                    _uiState.value = CharacterUiState.Success(
                        "✅ Character access OK! Found ${items.size} equipped items"
                    )
                },
                onFailure = { error ->
                    _uiState.value = CharacterUiState.Error(
                        "❌ Character access failed: ${error.message}"
                    )
                }
            )
        }
    }

    /**
     * Test accessing vault
     */
    fun testVaultAccess() {
        viewModelScope.launch {
            _uiState.value = CharacterUiState.Loading

            val result = loadoutRepository.getVaultItems()

            result.fold(
                onSuccess = { items ->
                    _uiState.value = CharacterUiState.Success(
                        "✅ Vault access OK! Found ${items.size} items"
                    )
                },
                onFailure = { error ->
                    _uiState.value = CharacterUiState.Error(
                        "❌ Vault access failed: ${error.message}"
                    )
                }
            )
        }
    }

    class Factory(
        private val loadoutRepository: LoadoutRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CharacterViewModel::class.java)) {
                return CharacterViewModel(loadoutRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

