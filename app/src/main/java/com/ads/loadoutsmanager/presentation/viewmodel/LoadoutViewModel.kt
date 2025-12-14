package com.ads.loadoutsmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ads.loadoutsmanager.data.model.DestinyLoadout
import com.ads.loadoutsmanager.data.repository.LoadoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing loadouts UI state and operations
 */
class LoadoutViewModel(
    private val repository: LoadoutRepository
) : ViewModel() {
    
    private val _loadouts = MutableStateFlow<List<DestinyLoadout>>(emptyList())
    val loadouts: StateFlow<List<DestinyLoadout>> = _loadouts.asStateFlow()
    
    private val _selectedLoadout = MutableStateFlow<DestinyLoadout?>(null)
    val selectedLoadout: StateFlow<DestinyLoadout?> = _selectedLoadout.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    /**
     * Load all loadouts for a character
     */
    fun loadLoadouts(characterId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val loadouts = repository.getLoadouts(characterId)
                _loadouts.value = loadouts
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load loadouts: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Select a loadout
     */
    fun selectLoadout(loadout: DestinyLoadout) {
        _selectedLoadout.value = loadout
    }
    
    /**
     * Create a new loadout
     */
    fun createLoadout(loadout: DestinyLoadout) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.createLoadout(loadout)
                    .onSuccess {
                        loadLoadouts(loadout.characterId)
                        _error.value = null
                    }
                    .onFailure {
                        _error.value = "Failed to create loadout: ${it.message}"
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update an existing loadout
     */
    fun updateLoadout(loadout: DestinyLoadout) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateLoadout(loadout)
                    .onSuccess {
                        loadLoadouts(loadout.characterId)
                        _error.value = null
                    }
                    .onFailure {
                        _error.value = "Failed to update loadout: ${it.message}"
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Delete a loadout
     */
    fun deleteLoadout(loadoutId: String, characterId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteLoadout(loadoutId)
                    .onSuccess {
                        loadLoadouts(characterId)
                        _error.value = null
                    }
                    .onFailure {
                        _error.value = "Failed to delete loadout: ${it.message}"
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Equip a loadout
     */
    fun equipLoadout(loadout: DestinyLoadout) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.equipLoadout(loadout)
                    .onSuccess {
                        loadLoadouts(loadout.characterId)
                        _error.value = null
                    }
                    .onFailure {
                        _error.value = "Failed to equip loadout: ${it.message}"
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Unequip a loadout and store in vault
     */
    fun unequipToVault(loadout: DestinyLoadout) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.unequipLoadoutToVault(loadout)
                    .onSuccess {
                        loadLoadouts(loadout.characterId)
                        _error.value = null
                    }
                    .onFailure {
                        _error.value = "Failed to store loadout in vault: ${it.message}"
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }
}
