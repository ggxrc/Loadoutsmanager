package com.ads.loadoutsmanager.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ads.loadoutsmanager.data.model.DestinyCharacter
import com.ads.loadoutsmanager.data.model.DestinyLoadout
import com.ads.loadoutsmanager.presentation.ui.theme.*
import com.ads.loadoutsmanager.presentation.viewmodel.LoadoutViewModel

/**
 * Heavily styled main screen with Destiny 2 aesthetic
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledMainScreen(
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
    var showDeleteConfirmation by remember { mutableStateOf<DestinyLoadout?>(null) }

    Scaffold(
        modifier = modifier,
        containerColor = DestinyDarkGray,
        topBar = {
            StyledTopBar(
                displayName = displayName,
                onLogout = onLogout
            )
        },
        floatingActionButton = {
            if (selectedCharacter != null) {
                FloatingActionButton(
                    onClick = {
                        loadoutToEdit = null
                        showCreateDialog = true
                    },
                    containerColor = DestinyGold,
                    contentColor = DestinyDarkGray,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Create Loadout",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is LoadoutViewModel.LoadoutUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = DestinyGold)
                            Text(
                                text = "Loading...",
                                color = Color.Gray
                            )
                        }
                    }
                }

                is LoadoutViewModel.LoadoutUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "❌",
                                fontSize = 48.sp
                            )
                            Text(
                                text = "Error: ${state.message}",
                                color = Color.Red,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { loadoutViewModel.loadCharacters() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DestinyGold,
                                    contentColor = DestinyDarkGray
                                )
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }

                is LoadoutViewModel.LoadoutUiState.Success -> {
                    if (characters.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Character selector
                            StyledCharacterSelector(
                                characters = characters,
                                selectedCharacter = selectedCharacter,
                                onCharacterSelected = { loadoutViewModel.selectCharacter(it) }
                            )

                            // Loadouts section
                            if (loadouts.isEmpty()) {
                                StyledEmptyLoadoutsState()
                            } else {
                                StyledLoadoutsList(
                                    loadouts = loadouts,
                                    onEquipLoadout = { loadoutViewModel.equipLoadout(it) },
                                    onEditLoadout = { loadout ->
                                        loadoutToEdit = loadout
                                        showCreateDialog = true
                                    },
                                    onDeleteLoadout = { showDeleteConfirmation = it }
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No characters found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
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

    // Delete confirmation dialog
    showDeleteConfirmation?.let { loadout ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text("Delete Loadout?") },
            text = { Text("Are you sure you want to delete '${loadout.name}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        loadoutViewModel.deleteLoadout(loadout.id)
                        showDeleteConfirmation = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StyledTopBar(
    displayName: String,
    onLogout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DestinyMediumGray,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LOADOUTS MANAGER",
                    style = MaterialTheme.typography.titleLarge,
                    color = DestinyGold,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Guardian: $displayName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
            }

            IconButton(
                onClick = onLogout,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = "Logout"
                )
            }
        }
    }
}

@Composable
private fun StyledCharacterSelector(
    characters: List<DestinyCharacter>,
    selectedCharacter: DestinyCharacter?,
    onCharacterSelected: (DestinyCharacter) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DestinyDarkGray
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "SELECT GUARDIAN",
                style = MaterialTheme.typography.titleMedium,
                color = DestinyGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(characters) { character ->
                    StyledCharacterCard(
                        character = character,
                        isSelected = character.characterId == selectedCharacter?.characterId,
                        onClick = { onCharacterSelected(character) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StyledCharacterCard(
    character: DestinyCharacter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                DestinyMediumGray
            else
                DestinyLightGray
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(3.dp, DestinyGold)
        else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Class icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            if (isSelected) DestinyGold.copy(alpha = 0.2f) else Color.Transparent,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (character.classType) {
                            0 -> "⚔️"
                            1 -> "🏹"
                            2 -> "✨"
                            else -> "?"
                        },
                        fontSize = 36.sp
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = when (character.classType) {
                            0 -> "TITAN"
                            1 -> "HUNTER"
                            2 -> "WARLOCK"
                            else -> "UNKNOWN"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isSelected) DestinyGold else Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡",
                            fontSize = 14.sp
                        )
                        Text(
                            text = character.light.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = DestinyGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StyledLoadoutsList(
    loadouts: List<DestinyLoadout>,
    onEquipLoadout: (DestinyLoadout) -> Unit,
    onEditLoadout: (DestinyLoadout) -> Unit,
    onDeleteLoadout: (DestinyLoadout) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOADOUTS",
                    style = MaterialTheme.typography.titleMedium,
                    color = DestinyGold,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${loadouts.size} total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        items(loadouts) { loadout ->
            StyledLoadoutCard(
                loadout = loadout,
                onEquip = { onEquipLoadout(loadout) },
                onEdit = { onEditLoadout(loadout) },
                onDelete = { onDeleteLoadout(loadout) }
            )
        }
    }
}

@Composable
private fun StyledLoadoutCard(
    loadout: DestinyLoadout,
    onEquip: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (loadout.isEquipped)
                DestinyMediumGray
            else
                DestinyLightGray
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (loadout.isEquipped)
            androidx.compose.foundation.BorderStroke(2.dp, DestinyGold)
        else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (loadout.isEquipped) 6.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (loadout.isEquipped)
                            Brush.horizontalGradient(
                                colors = listOf(
                                    DestinyGold.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        else Brush.horizontalGradient(
                            colors = listOf(
                                DestinyBlue.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = loadout.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (loadout.isEquipped) DestinyGold else Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            if (loadout.isEquipped) {
                                Surface(
                                    color = DestinyGold,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "EQUIPPED",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DestinyDarkGray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        if (loadout.description != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = loadout.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }

            // Items info and actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Items count
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📦", fontSize = 16.sp)
                        Text(
                            text = "${loadout.equipment.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!loadout.isEquipped) {
                        Button(
                            onClick = onEquip,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DestinyGold,
                                contentColor = DestinyDarkGray
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("EQUIP", fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = onEdit,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = DestinyBlue
                        )
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }

                    IconButton(
                        onClick = onDelete,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

@Composable
private fun StyledEmptyLoadoutsState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🎯",
                fontSize = 72.sp
            )

            Text(
                text = "No Loadouts Yet",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Create your first loadout by tapping the + button",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

