package com.ads.loadoutsmanager.presentation.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ads.loadoutsmanager.data.model.DestinyLoadout
import com.ads.loadoutsmanager.data.model.LoadoutEquipment
import com.ads.loadoutsmanager.ui.theme.SciFiDarkSurfaceVariant

enum class LoadoutViewState {
    LIST,
    DETAIL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadoutDetailScreen(
    loadout: DestinyLoadout,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var viewState by remember { mutableStateOf(LoadoutViewState.DETAIL) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = loadout.name,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Crossfade(
            targetState = viewState,
            modifier = Modifier.padding(paddingValues)
        ) { state ->
            when (state) {
                LoadoutViewState.LIST -> {
                    LoadoutEquipmentList(equipment = loadout.equipment)
                }
                LoadoutViewState.DETAIL -> {
                    LoadoutEquipmentDetail(equipment = loadout.equipment)
                }
            }
        }
    }
}

@Composable
private fun LoadoutEquipmentList(
    equipment: LoadoutEquipment,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Weapons Section
        item {
            SectionHeader(title = "Weapons")
        }
        
        equipment.kineticWeapon?.let { item ->
            item {
                ItemCard(
                    item = item,
                    itemName = "Kinetic Weapon"
                )
            }
        }
        
        equipment.energyWeapon?.let { item ->
            item {
                ItemCard(
                    item = item,
                    itemName = "Energy Weapon"
                )
            }
        }
        
        equipment.powerWeapon?.let { item ->
            item {
                ItemCard(
                    item = item,
                    itemName = "Power Weapon"
                )
            }
        }
        
        // Armor Section
        item {
            SectionHeader(
                title = "Armor",
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        
        equipment.helmet?.let { item ->
            item {
                ItemCard(
                    item = item,
                    itemName = "Helmet"
                )
            }
        }
        
        equipment.gauntlets?.let { item ->
            item {
                ItemCard(
                    item = item,
                    itemName = "Gauntlets"
                )
            }
        }
        
        equipment.chestArmor?.let { item ->
            item {
                ItemCard(
                    item = item,
                    itemName = "Chest Armor"
                )
            }
        }
        
        equipment.legArmor?.let { item ->
            item {
                ItemCard(
                    item = item,
                    itemName = "Leg Armor"
                )
            }
        }
        
        equipment.classItem?.let { item ->
            item {
                ItemCard(
                    item = item,
                    itemName = "Class Item"
                )
            }
        }
    }
}

@Composable
private fun LoadoutEquipmentDetail(
    equipment: LoadoutEquipment,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Weapons Grid
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = SciFiDarkSurfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Weapons",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    equipment.kineticWeapon?.let {
                        EquipmentSlot(
                            slotName = "Kinetic",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    equipment.energyWeapon?.let {
                        EquipmentSlot(
                            slotName = "Energy",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    equipment.powerWeapon?.let {
                        EquipmentSlot(
                            slotName = "Power",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
        
        // Armor Grid
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = SciFiDarkSurfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Armor",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        equipment.helmet?.let {
                            EquipmentSlot(
                                slotName = "Helmet",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        equipment.gauntlets?.let {
                            EquipmentSlot(
                                slotName = "Gauntlets",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        equipment.chestArmor?.let {
                            EquipmentSlot(
                                slotName = "Chest",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        equipment.legArmor?.let {
                            EquipmentSlot(
                                slotName = "Legs",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    equipment.classItem?.let {
                        EquipmentSlot(
                            slotName = "Class Item",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EquipmentSlot(
    slotName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = slotName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}
