package com.ads.loadoutsmanager.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ads.loadoutsmanager.data.api.ManifestService
import com.ads.loadoutsmanager.data.model.DestinyItem
import com.ads.loadoutsmanager.data.model.DestinyLoadout
import com.ads.loadoutsmanager.data.repository.LoadoutRepository
import com.ads.loadoutsmanager.presentation.viewmodel.ItemSelectorViewModel
import java.util.UUID

/**
 * Dialog for creating or editing a loadout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLoadoutDialog(
    characterId: String,
    loadoutRepository: LoadoutRepository,
    manifestService: ManifestService,
    existingLoadout: DestinyLoadout? = null,
    onConfirm: (DestinyLoadout) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemSelectorViewModel: ItemSelectorViewModel = viewModel(
        factory = ItemSelectorViewModel.Factory(
            loadoutRepository = loadoutRepository,
            characterId = characterId
        )
    )

    var loadoutName by remember { mutableStateOf(existingLoadout?.name ?: "") }
    var loadoutDescription by remember { mutableStateOf(existingLoadout?.description ?: "") }
    var showItemSelector by remember { mutableStateOf(false) }

    // Track selected items locally
    var selectedItems by remember {
        mutableStateOf(existingLoadout?.equipment ?: emptyList())
    }

    val equippedItems by itemSelectorViewModel.equippedItems.collectAsState()
    val inventoryItems by itemSelectorViewModel.inventoryItems.collectAsState()
    val vaultItems by itemSelectorViewModel.vaultItems.collectAsState()
    val itemSelectorState by itemSelectorViewModel.uiState.collectAsState()
    val vaultPage by itemSelectorViewModel.vaultPage.collectAsState()
    val vaultHasMore by itemSelectorViewModel.vaultHasMore.collectAsState()
    val isLoading by itemSelectorViewModel.isLoading.collectAsState()
    
    // Update character ID and load items when dialog opens or character changes
    LaunchedEffect(characterId) {
        itemSelectorViewModel.updateCharacterId(characterId)
        itemSelectorViewModel.loadItems()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = MaterialTheme.shapes.large
        ) {
            if (isLoading) {
                // Show loading while items are being fetched
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading items...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                // Show dialog content when items are loaded
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Text(
                        text = if (existingLoadout != null) "Edit Loadout" else "Create Loadout",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                Spacer(modifier = Modifier.height(16.dp))

                // Name field
                OutlinedTextField(
                    value = loadoutName,
                    onValueChange = { loadoutName = it },
                    label = { Text("Loadout Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description field
                OutlinedTextField(
                    value = loadoutDescription,
                    onValueChange = { loadoutDescription = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selected items count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Selected Items: ${selectedItems.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Button(onClick = { showItemSelector = true }) {
                        Text("Add Items")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Show selected items summary
                if (selectedItems.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "Items in this loadout:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Group by weapon/armor
                            val weapons = selectedItems.count {
                                it.bucketHash in setOf(1498876634L, 2465295065L, 953998645L)
                            }
                            val armor = selectedItems.count {
                                it.bucketHash in setOf(3448274439L, 3551918588L, 14239492L, 20886954L, 1585787867L)
                            }

                            Text(
                                text = "• $weapons Weapons, $armor Armor pieces",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (loadoutName.isNotBlank() && selectedItems.isNotEmpty()) {
                                val loadout = DestinyLoadout(
                                    id = existingLoadout?.id ?: UUID.randomUUID().toString(),
                                    name = loadoutName,
                                    description = loadoutDescription.ifBlank { null },
                                    characterId = characterId,
                                    equipment = selectedItems,
                                    isEquipped = false,
                                    createdAt = existingLoadout?.createdAt ?: System.currentTimeMillis(),
                                    updatedAt = System.currentTimeMillis()
                                )
                                onConfirm(loadout)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = loadoutName.isNotBlank() && selectedItems.isNotEmpty()
                    ) {
                        Text(if (existingLoadout != null) "Update" else "Create")
                    }
                }
            }
        }
    }
    // Close the if/else for loading state
    }

    var selectedItemForDetails by remember { mutableStateOf<DestinyItem?>(null) }

    // Item selector dialog
    if (showItemSelector) {
        ItemSelectorDialog(
            characterId = characterId,
            equippedItems = equippedItems,
            inventoryItems = inventoryItems,
            vaultItems = vaultItems,
            selectedItems = selectedItems,
            vaultPage = vaultPage,
            vaultHasMore = vaultHasMore,
            onItemToggle = { item ->
                val newSelection = selectedItems.toMutableList()
                val existingIndex = newSelection.indexOfFirst {
                    it.itemInstanceId == item.itemInstanceId
                }
                if (existingIndex >= 0) {
                    newSelection.removeAt(existingIndex)
                } else {
                    newSelection.add(item)
                }
                selectedItems = newSelection
            },
            onItemClick = { item ->
                selectedItemForDetails = item
            },
            onNextVaultPage = {
                itemSelectorViewModel.loadNextVaultPage()
            },
            onPreviousVaultPage = {
                itemSelectorViewModel.loadPreviousVaultPage()
            },
            onSyncVault = {
                itemSelectorViewModel.syncVaultFromAPI()
            },
            onConfirm = {
                showItemSelector = false
            },
            onDismiss = {
                showItemSelector = false
            }
        )
    }

    // Item detail dialog
    selectedItemForDetails?.let { item ->
        ItemDetailDialog(
            item = item,
            manifestService = manifestService,
            onDismiss = { selectedItemForDetails = null }
        )
    }
}

