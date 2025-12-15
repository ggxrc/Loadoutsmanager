package com.ads.loadoutsmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ads.loadoutsmanager.data.model.DestinyCharacter
import com.ads.loadoutsmanager.data.model.DestinyLoadout
import com.ads.loadoutsmanager.data.repository.LoadoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing loadouts across multiple characters
 */
class LoadoutViewModel(
    private val loadoutRepository: LoadoutRepository,
    private val membershipType: Int,
    private val membershipId: String
) : ViewModel() {

    private val _characters = MutableStateFlow<List<DestinyCharacter>>(emptyList())
    val characters: StateFlow<List<DestinyCharacter>> = _characters.asStateFlow()

    private val _selectedCharacter = MutableStateFlow<DestinyCharacter?>(null)
    val selectedCharacter: StateFlow<DestinyCharacter?> = _selectedCharacter.asStateFlow()

    private val _loadouts = MutableStateFlow<List<DestinyLoadout>>(emptyList())
    val loadouts: StateFlow<List<DestinyLoadout>> = _loadouts.asStateFlow()

    private val _uiState = MutableStateFlow<LoadoutUiState>(LoadoutUiState.Loading)
    val uiState: StateFlow<LoadoutUiState> = _uiState.asStateFlow()

    sealed class LoadoutUiState {
        object Loading : LoadoutUiState()
        object Success : LoadoutUiState()
        data class Error(val message: String) : LoadoutUiState()
    }

    init {
        loadCharacters()
    }

    /**
     * Load all characters from the API
     */
    fun loadCharacters() {
        viewModelScope.launch {
            _uiState.value = LoadoutUiState.Loading

            try {
                val result = loadoutRepository.getCharacters()
                result.fold(
                    onSuccess = { characterList ->
                        android.util.Log.d("LoadoutViewModel", "✅ Loaded ${characterList.size} characters")
                        _characters.value = characterList

                        // Auto-select first character
                        if (characterList.isNotEmpty() && _selectedCharacter.value == null) {
                            selectCharacter(characterList[0])
                        }

                        _uiState.value = LoadoutUiState.Success
                    },
                    onFailure = { error ->
                        android.util.Log.e("LoadoutViewModel", "❌ Failed to load characters: ${error.message}")
                        _uiState.value = LoadoutUiState.Error(error.message ?: "Failed to load characters")
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("LoadoutViewModel", "❌ Exception loading characters", e)
                _uiState.value = LoadoutUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Select a character and load their loadouts
     */
    fun selectCharacter(character: DestinyCharacter) {
        android.util.Log.d("LoadoutViewModel", "📌 Selecting character: ${character.characterId}")
        _selectedCharacter.value = character
        loadLoadoutsForCharacter(character.characterId)
    }

    /**
     * Load loadouts for the selected character
     */
    private fun loadLoadoutsForCharacter(characterId: String) {
        viewModelScope.launch {
            try {
                val loadoutList = loadoutRepository.getLoadouts(characterId)
                android.util.Log.d("LoadoutViewModel", "✅ Loaded ${loadoutList.size} loadouts for character $characterId")
                _loadouts.value = loadoutList
            } catch (e: Exception) {
                android.util.Log.e("LoadoutViewModel", "❌ Failed to load loadouts", e)
                _loadouts.value = emptyList()
            }
        }
    }

    /**
     * Create a new loadout
     */
    fun createLoadout(loadout: DestinyLoadout) {
        viewModelScope.launch {
            try {
                loadoutRepository.createLoadout(loadout).fold(
                    onSuccess = {
                        android.util.Log.d("LoadoutViewModel", "✅ Loadout created: ${loadout.name}")
                        // Reload loadouts
                        _selectedCharacter.value?.let { char ->
                            loadLoadoutsForCharacter(char.characterId)
                        }
                    },
                    onFailure = { error ->
                        android.util.Log.e("LoadoutViewModel", "❌ Failed to create loadout: ${error.message}")
                        _uiState.value = LoadoutUiState.Error(error.message ?: "Failed to create loadout")
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("LoadoutViewModel", "❌ Exception creating loadout", e)
                _uiState.value = LoadoutUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Update an existing loadout
     */
    fun updateLoadout(loadout: DestinyLoadout) {
        viewModelScope.launch {
            try {
                loadoutRepository.updateLoadout(loadout).fold(
                    onSuccess = {
                        android.util.Log.d("LoadoutViewModel", "✅ Loadout updated: ${loadout.name}")
                        // Reload loadouts
                        _selectedCharacter.value?.let { char ->
                            loadLoadoutsForCharacter(char.characterId)
                        }
                    },
                    onFailure = { error ->
                        android.util.Log.e("LoadoutViewModel", "❌ Failed to update loadout: ${error.message}")
                        _uiState.value = LoadoutUiState.Error(error.message ?: "Failed to update loadout")
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("LoadoutViewModel", "❌ Exception updating loadout", e)
                _uiState.value = LoadoutUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Delete a loadout
     */
    fun deleteLoadout(loadoutId: String) {
        viewModelScope.launch {
            try {
                loadoutRepository.deleteLoadout(loadoutId).fold(
                    onSuccess = {
                        android.util.Log.d("LoadoutViewModel", "✅ Loadout deleted: $loadoutId")
                        // Reload loadouts
                        _selectedCharacter.value?.let { char ->
                            loadLoadoutsForCharacter(char.characterId)
                        }
                    },
                    onFailure = { error ->
                        android.util.Log.e("LoadoutViewModel", "❌ Failed to delete loadout: ${error.message}")
                        _uiState.value = LoadoutUiState.Error(error.message ?: "Failed to delete loadout")
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("LoadoutViewModel", "❌ Exception deleting loadout", e)
                _uiState.value = LoadoutUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Equip a loadout (quick-equip)
     */
    fun equipLoadout(loadout: DestinyLoadout) {
        viewModelScope.launch {
            _uiState.value = LoadoutUiState.Loading

            try {
                val allCharacterIds = _characters.value.map { it.characterId }
                loadoutRepository.equipLoadout(loadout, allCharacterIds).fold(
                    onSuccess = {
                        android.util.Log.d("LoadoutViewModel", "✅ Loadout equipped: ${loadout.name}")
                        _uiState.value = LoadoutUiState.Success

                        // Reload loadouts to update equipped status
                        _selectedCharacter.value?.let { char ->
                            loadLoadoutsForCharacter(char.characterId)
                        }
                    },
                    onFailure = { error ->
                        android.util.Log.e("LoadoutViewModel", "❌ Failed to equip loadout: ${error.message}")
                        _uiState.value = LoadoutUiState.Error(error.message ?: "Failed to equip loadout")
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("LoadoutViewModel", "❌ Exception equipping loadout", e)
                _uiState.value = LoadoutUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    class Factory(
        private val loadoutRepository: LoadoutRepository,
        private val membershipType: Int,
        private val membershipId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoadoutViewModel::class.java)) {
                return LoadoutViewModel(loadoutRepository, membershipType, membershipId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

