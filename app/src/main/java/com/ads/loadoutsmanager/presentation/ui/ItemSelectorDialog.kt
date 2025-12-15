package com.ads.loadoutsmanager.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.ads.loadoutsmanager.data.model.DestinyItem
import com.ads.loadoutsmanager.data.model.ItemCategory
import com.ads.loadoutsmanager.data.model.ItemLocation
import com.ads.loadoutsmanager.data.model.ItemSubcategory
import com.ads.loadoutsmanager.presentation.ui.theme.*

/**
 * Heavily styled item selector with categories, subcategories and item details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSelectorDialog(
    characterId: String,
    equippedItems: List<DestinyItem>,
    inventoryItems: List<DestinyItem>,
    vaultItems: List<DestinyItem>,
    selectedItems: List<DestinyItem>,
    onItemToggle: (DestinyItem) -> Unit,
    onItemClick: (DestinyItem) -> Unit,
    vaultPage: Int = 0,
    vaultHasMore: Boolean = false,
    onNextVaultPage: () -> Unit = {},
    onPreviousVaultPage: () -> Unit = {},
    onSyncVault: () -> Unit = {},
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLocation by remember { mutableStateOf(0) } // 0=Equipped, 1=Inventory, 2=Vault
    var selectedCategory by remember { mutableStateOf<ItemCategory?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.98f)
                .fillMaxHeight(0.95f),
            shape = RoundedCornerShape(16.dp),
            color = DestinyDarkGray
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                ItemSelectorHeader(
                    selectedCount = selectedItems.size,
                    onDismiss = onDismiss,
                    showVaultSync = selectedLocation == 2,
                    onSyncVault = onSyncVault
                )

                // Location tabs (Equipped/Inventory/Vault)
                LocationTabs(
                    selectedTab = selectedLocation,
                    onTabSelected = { selectedLocation = it },
                    equippedCount = equippedItems.size,
                    inventoryCount = inventoryItems.size,
                    vaultCount = vaultItems.size
                )

                // Category selector (Weapons/Armor)
                CategorySelector(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )

                // Items grid
                Box(modifier = Modifier.weight(1f)) {
                    val currentItems = when (selectedLocation) {
                        0 -> equippedItems
                        1 -> inventoryItems
                        else -> vaultItems
                    }

                    ItemsGrid(
                        items = currentItems,
                        selectedCategory = selectedCategory,
                        selectedItems = selectedItems,
                        onItemToggle = onItemToggle,
                        onItemClick = onItemClick
                    )
                }
                
                // Vault pagination controls
                if (selectedLocation == 2 && (vaultPage > 0 || vaultHasMore)) {
                    VaultPaginationControls(
                        currentPage = vaultPage,
                        hasPrevious = vaultPage > 0,
                        hasNext = vaultHasMore,
                        onPrevious = onPreviousVaultPage,
                        onNext = onNextVaultPage
                    )
                }

                // Action buttons
                ActionButtons(
                    onConfirm = onConfirm,
                    onDismiss = onDismiss,
                    selectedCount = selectedItems.size
                )
            }
        }
    }
}

@Composable
private fun ItemSelectorHeader(
    selectedCount: Int,
    onDismiss: () -> Unit,
    showVaultSync: Boolean = false,
    onSyncVault: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DestinyMediumGray,
                        DestinyDarkGray
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
            Column {
                Text(
                    text = "SELECT EQUIPMENT",
                    style = MaterialTheme.typography.titleLarge,
                    color = DestinyGold,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$selectedCount items selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sync vault button (only show when vault tab is active)
                if (showVaultSync) {
                    OutlinedButton(
                        onClick = onSyncVault,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = DestinyGold
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DestinyGold)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Sync Vault",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SYNC VAULT")
                    }
                }
                
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    equippedCount: Int,
    inventoryCount: Int,
    vaultCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DestinyMediumGray)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LocationTab(
            text = "EQUIPPED",
            count = equippedCount,
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            modifier = Modifier.weight(1f)
        )

        LocationTab(
            text = "INVENTORY",
            count = inventoryCount,
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            modifier = Modifier.weight(1f)
        )

        LocationTab(
            text = "VAULT",
            count = vaultCount,
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LocationTab(
    text: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        color = if (selected) DestinyGold.copy(alpha = 0.2f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        border = if (selected)
            androidx.compose.foundation.BorderStroke(2.dp, DestinyGold)
        else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) DestinyGold else Color.LightGray,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = if (selected) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CategorySelector(
    selectedCategory: ItemCategory?,
    onCategorySelected: (ItemCategory?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DestinyDarkGray)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryButton(
            text = "ALL",
            icon = "📦",
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            modifier = Modifier.weight(1f)
        )

        CategoryButton(
            text = "WEAPONS",
            icon = "⚔️",
            selected = selectedCategory == ItemCategory.WEAPON,
            onClick = { onCategorySelected(ItemCategory.WEAPON) },
            modifier = Modifier.weight(1f)
        )

        CategoryButton(
            text = "ARMOR",
            icon = "🛡️",
            selected = selectedCategory == ItemCategory.ARMOR,
            onClick = { onCategorySelected(ItemCategory.ARMOR) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CategoryButton(
    text: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clickable(onClick = onClick),
        color = if (selected) DestinyBlue.copy(alpha = 0.3f) else DestinyMediumGray,
        shape = RoundedCornerShape(8.dp),
        border = if (selected)
            androidx.compose.foundation.BorderStroke(2.dp, DestinyBlue)
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            ) {
            Text(
                text = icon,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) Color.White else Color.LightGray,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ItemsGrid(
    items: List<DestinyItem>,
    selectedCategory: ItemCategory?,
    selectedItems: List<DestinyItem>,
    onItemToggle: (DestinyItem) -> Unit,
    onItemClick: (DestinyItem) -> Unit
) {
    val filteredItems = if (selectedCategory != null) {
        val bucketHashes = selectedCategory.getSubcategories().map { it.bucketHash }
        items.filter { it.bucketHash in bucketHashes }
    } else {
        items
    }

    if (filteredItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🔍",
                    fontSize = 48.sp
                )
                Text(
                    text = "No items found",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(12.dp)
        ) {
            // Group items by subcategory
            val subcategories = if (selectedCategory != null) {
                selectedCategory.getSubcategories()
            } else {
                ItemSubcategory.values().toList()
            }

            subcategories.forEach { subcategory ->
                val subcategoryItems = filteredItems.filter { it.bucketHash == subcategory.bucketHash }

                if (subcategoryItems.isNotEmpty()) {
                    item {
                        SubcategorySection(
                            subcategory = subcategory,
                            items = subcategoryItems,
                            selectedItems = selectedItems,
                            onItemToggle = onItemToggle,
                            onItemClick = onItemClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubcategorySection(
    subcategory: ItemSubcategory,
    items: List<DestinyItem>,
    selectedItems: List<DestinyItem>,
    onItemToggle: (DestinyItem) -> Unit,
    onItemClick: (DestinyItem) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Subcategory header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DestinyMediumGray,
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subcategory.displayName.uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    color = DestinyGold,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${items.size} items",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        // Items grid for this subcategory
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.heightIn(max = 1000.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(items) { item ->
                ItemCard(
                    item = item,
                    isSelected = selectedItems.any { it.itemInstanceId == item.itemInstanceId },
                    onToggle = { onItemToggle(item) },
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
private fun ItemCard(
    item: DestinyItem,
    isSelected: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DestinyGold.copy(alpha = 0.2f) else DestinyLightGray
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(2.dp, DestinyGold)
        else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Item icon placeholder (will be replaced with actual image loading)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DestinyMediumGray),
                contentAlignment = Alignment.Center
            ) {
                if (item.iconUrl != null) {
                    AsyncImage(
                        model = "https://www.bungie.net${item.iconUrl}",
                        contentDescription = getItemNameFromBucket(item.bucketHash),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback to icon
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Icon based on bucket
                        Text(
                            text = getItemIcon(item.bucketHash),
                            fontSize = 32.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Item name (truncated)
                        Text(
                            text = getItemNameFromBucket(item.bucketHash),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            // Selection checkbox
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(24.dp)
                        .background(DestinyGold, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = DestinyDarkGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Toggle button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(28.dp)
                        .clickable(onClick = onToggle),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) DestinyGold.copy(alpha = 0.8f) else DestinyMediumGray
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isSelected) "−" else "+",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    selectedCount: Int
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
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
            ) {
                Text("CANCEL")
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
                enabled = selectedCount > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DestinyGold,
                    contentColor = DestinyDarkGray,
                    disabledContainerColor = DestinyMediumGray,
                    disabledContentColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "ADD TO LOADOUT ($selectedCount)",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun VaultPaginationControls(
    currentPage: Int,
    hasPrevious: Boolean,
    hasNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DestinyMediumGray,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            OutlinedButton(
                onClick = onPrevious,
                enabled = hasPrevious,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DestinyGold,
                    disabledContentColor = Color.Gray
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (hasPrevious) DestinyGold else Color.Gray
                )
            ) {
                Text("◀ PREVIOUS")
            }
            
            // Page indicator
            Text(
                text = "Page ${currentPage + 1}",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = DestinyGold,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )
            
            // Next button
            OutlinedButton(
                onClick = onNext,
                enabled = hasNext,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DestinyGold,
                    disabledContentColor = Color.Gray
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (hasNext) DestinyGold else Color.Gray
                )
            ) {
                Text("NEXT ▶")
            }
        }
    }
}

private fun getItemIcon(bucketHash: Long): String {
    return when (bucketHash) {
        1498876634L -> "🔫" // Kinetic
        2465295065L -> "⚡" // Energy
        953998645L -> "💥" // Power
        3448274439L -> "⛑️" // Helmet
        3551918588L -> "🧤" // Gauntlets
        14239492L -> "🦺" // Chest
        20886954L -> "👖" // Legs
        1585787867L -> "🎽" // Class Item
        else -> "📦"
    }
}

/**
 * Get item slot name from bucket hash
 */
private fun getItemNameFromBucket(bucketHash: Long): String {
    return when (bucketHash) {
        1498876634L -> "Kinetic"
        2465295065L -> "Energy"
        953998645L -> "Power"
        3448274439L -> "Helmet"
        3551918588L -> "Gauntlets"
        14239492L -> "Chest"
        20886954L -> "Legs"
        1585787867L -> "Class"
        else -> "Item"
    }
}

