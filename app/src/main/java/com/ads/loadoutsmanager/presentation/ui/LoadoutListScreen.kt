package com.ads.loadoutsmanager.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ads.loadoutsmanager.data.model.DestinyLoadout

/**
 * Main screen for displaying and managing loadouts
 */
@Composable
fun LoadoutListScreen(
    loadouts: List<DestinyLoadout>,
    isLoading: Boolean,
    error: String?,
    onLoadoutClick: (DestinyLoadout) -> Unit,
    onEquipLoadout: (DestinyLoadout) -> Unit,
    onDeleteLoadout: (DestinyLoadout) -> Unit,
    onCreateLoadout: () -> Unit,
    onClearError: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Destiny 2 Loadouts") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateLoadout) {
                Text("+")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    ErrorMessage(
                        error = error,
                        onDismiss = onClearError
                    )
                }
                loadouts.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LoadoutList(
                        loadouts = loadouts,
                        onLoadoutClick = onLoadoutClick,
                        onEquipLoadout = onEquipLoadout,
                        onDeleteLoadout = onDeleteLoadout
                    )
                }
            }
        }
    }
}

@Composable
fun LoadoutList(
    loadouts: List<DestinyLoadout>,
    onLoadoutClick: (DestinyLoadout) -> Unit,
    onEquipLoadout: (DestinyLoadout) -> Unit,
    onDeleteLoadout: (DestinyLoadout) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(loadouts, key = { it.id }) { loadout ->
            LoadoutItem(
                loadout = loadout,
                onClick = { onLoadoutClick(loadout) },
                onEquip = { onEquipLoadout(loadout) },
                onDelete = { onDeleteLoadout(loadout) }
            )
        }
    }
}

@Composable
fun LoadoutItem(
    loadout: DestinyLoadout,
    onClick: () -> Unit,
    onEquip: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = loadout.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    loadout.description?.let { desc ->
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${loadout.equipment.size} items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (loadout.isEquipped) {
                        Text(
                            text = "Currently Equipped",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onEquip,
                    enabled = !loadout.isEquipped
                ) {
                    Text(if (loadout.isEquipped) "Equipped" else "Equip")
                }
            }
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No loadouts yet",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create your first loadout to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ErrorMessage(
    error: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(error) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
