package com.ads.loadoutsmanager.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ads.loadoutsmanager.data.model.DestinyCharacter
import com.ads.loadoutsmanager.data.model.DestinyLoadout
import com.ads.loadoutsmanager.presentation.viewmodel.LoadoutViewModel

/**
 * Main screen showing user's loadouts and characters
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    displayName: String,
    loadoutViewModel: LoadoutViewModel,
    loadoutRepository: com.ads.loadoutsmanager.data.repository.LoadoutRepository,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val characters by loadoutViewModel.characters.collectAsState()
    val selectedCharacter by loadoutViewModel.selectedCharacter.collectAsState()
    val loadouts by loadoutViewModel.loadouts.collectAsState()
    val uiState by loadoutViewModel.uiState.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var loadoutToEdit by remember { mutableStateOf<DestinyLoadout?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loadouts Manager") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedCharacter != null) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Loadout")
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // ...existing code...

            // Welcome message
            Text(
                text = "Welcome, $displayName!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Loading/Error states
            when (val state = uiState) {
                is LoadoutViewModel.LoadoutUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is LoadoutViewModel.LoadoutUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = { loadoutViewModel.loadCharacters() }) {
                        Text("Retry")
                    }
                }
                is LoadoutViewModel.LoadoutUiState.Success -> {
                    // Character selector
                    if (characters.isNotEmpty()) {
                        CharacterSelector(
                            characters = characters,
                            selectedCharacter = selectedCharacter,
                            onCharacterSelected = { loadoutViewModel.selectCharacter(it) }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Loadouts list
                        if (loadouts.isEmpty()) {
                            EmptyLoadoutsState()
                        } else {
                            LoadoutsList(
                                loadouts = loadouts,
                                onEquipLoadout = { loadoutViewModel.equipLoadout(it) },
                                onEditLoadout = { loadout ->
                                    loadoutToEdit = loadout
                                    showCreateDialog = true
                                },
                                onDeleteLoadout = { loadoutViewModel.deleteLoadout(it.id) }
                            )
                        }
                    } else {
                        Text(
                            text = "No characters found",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }

    // Create/Edit loadout dialog
    if (showCreateDialog && selectedCharacter != null) {
        CreateLoadoutDialog(
            characterId = selectedCharacter!!.characterId,
            loadoutRepository = loadoutRepository,
            existingLoadout = loadoutToEdit,
            onConfirm = { loadout ->
                if (loadoutToEdit != null) {
                    loadoutViewModel.updateLoadout(loadout)
                } else {
                    loadoutViewModel.createLoadout(loadout)
                }
                showCreateDialog = false
                loadoutToEdit = null
            },
            onDismiss = {
                showCreateDialog = false
                loadoutToEdit = null
            }
        )
    }
}

@Composable
private fun CharacterSelector(
    characters: List<DestinyCharacter>,
    selectedCharacter: DestinyCharacter?,
    onCharacterSelected: (DestinyCharacter) -> Unit
) {
    Column {
        Text(
            text = "Select Character",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(characters) { character ->
                CharacterCard(
                    character = character,
                    isSelected = character.characterId == selectedCharacter?.characterId,
                    onClick = { onCharacterSelected(character) }
                )
            }
        }
    }
}

@Composable
private fun CharacterCard(
    character: DestinyCharacter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Character class icon/text
            Text(
                text = when (character.classType) {
                    0 -> "⚔️ Titan"
                    1 -> "🏹 Hunter"
                    2 -> "✨ Warlock"
                    else -> "Unknown"
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Light level
            Text(
                text = "${character.light} ⚡",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LoadoutsList(
    loadouts: List<DestinyLoadout>,
    onEquipLoadout: (DestinyLoadout) -> Unit,
    onEditLoadout: (DestinyLoadout) -> Unit,
    onDeleteLoadout: (DestinyLoadout) -> Unit
) {
    Column {
        Text(
            text = "Loadouts (${loadouts.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(loadouts) { loadout ->
                LoadoutCard(
                    loadout = loadout,
                    onEquip = { onEquipLoadout(loadout) },
                    onEdit = { onEditLoadout(loadout) },
                    onDelete = { onDeleteLoadout(loadout) }
                )
            }
        }
    }
}

@Composable
private fun LoadoutCard(
    loadout: DestinyLoadout,
    onEquip: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (loadout.isEquipped)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = loadout.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (loadout.description != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = loadout.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${loadout.equipment.size} items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (loadout.isEquipped) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✓ Equipped",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!loadout.isEquipped) {
                    Button(onClick = onEquip) {
                        Text("Equip")
                    }
                }

                OutlinedButton(onClick = onEdit) {
                    Text("Edit")
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyLoadoutsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Loadouts Yet",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Create your first loadout by tapping the + button",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

