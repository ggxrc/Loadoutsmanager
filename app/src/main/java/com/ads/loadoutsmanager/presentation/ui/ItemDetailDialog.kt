  package com.ads.loadoutsmanager.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ads.loadoutsmanager.data.model.*
import com.ads.loadoutsmanager.presentation.ui.theme.*

/**
 * Detailed view of an item showing all stats, perks, and info
 * Similar to Destiny 2 in-game item inspection
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailDialog(
    item: DestinyItem,
    itemDetails: ItemDetails?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            color = DestinyDarkGray
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with close button
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
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                if (itemDetails != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Item header with name and power
                        item {
                            ItemHeaderSection(itemDetails)
                        }

                        // Item description
                        item {
                            Text(
                                text = itemDetails.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.LightGray,
                                lineHeight = 20.sp
                            )
                        }

                        // Stats section
                        item {
                            StatsSection(itemDetails.stats)
                        }

                        // Perks section
                        if (!itemDetails.perks.isNullOrEmpty()) {
                            item {
                                PerksSection(itemDetails.perks)
                            }
                        }

                        // Sockets section
                        if (!itemDetails.sockets.isNullOrEmpty()) {
                            item {
                                SocketsSection(itemDetails.sockets)
                            }
                        }

                        // Additional info
                        item {
                            AdditionalInfoSection(itemDetails)
                        }
                    }
                } else {
                    // Loading or placeholder
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
                                text = "Loading item details...",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemHeaderSection(item: ItemDetails) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Item name with rarity color
        Text(
            text = item.name,
            style = MaterialTheme.typography.headlineSmall,
            color = getRarityColor(item.tierType),
            fontWeight = FontWeight.Bold
        )

        // Power level and damage type
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Power level
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚡",
                    fontSize = 20.sp
                )
                Text(
                    text = item.power.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = DestinyGold,
                    fontWeight = FontWeight.Bold
                )
            }

            // Damage type
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = getDamageTypeColor(item.damageType).copy(alpha = 0.2f)
            ) {
                Text(
                    text = item.damageType.name,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = getDamageTypeColor(item.damageType),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
        }

        // Item type/subtype
        Text(
            text = "${item.itemType} • ${item.itemSubType}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
private fun StatsSection(stats: Map<String, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DestinyMediumGray
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "STATS",
                style = MaterialTheme.typography.titleMedium,
                color = DestinyGold,
                fontWeight = FontWeight.Bold
            )

            stats.forEach { (statName, statValue) ->
                StatBar(
                    name = statName,
                    value = statValue,
                    maxValue = 100
                )
            }
        }
    }
}

@Composable
private fun StatBar(
    name: String,
    value: Int,
    maxValue: Int
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        LinearProgressIndicator(
            progress = value.toFloat() / maxValue,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = DestinyGold,
            trackColor = DestinyLightGray
        )
    }
}

@Composable
private fun PerksSection(perks: List<PerkInfo>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DestinyMediumGray
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "PERKS",
                style = MaterialTheme.typography.titleMedium,
                color = DestinyGold,
                fontWeight = FontWeight.Bold
            )

            perks.forEach { perk ->
                PerkItem(perk)
            }
        }
    }
}

@Composable
private fun PerkItem(perk: PerkInfo) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Perk icon placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(DestinyLightGray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🎯",
                fontSize = 24.sp
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = perk.perkName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = perk.perkDescription,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun SocketsSection(sockets: List<SocketInfo>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DestinyMediumGray
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "MODS",
                style = MaterialTheme.typography.titleMedium,
                color = DestinyGold,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sockets.forEach { socket ->
                    SocketSlot(socket)
                }
            }
        }
    }
}

@Composable
private fun SocketSlot(socket: SocketInfo) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(
                if (socket.plugHash != null) DestinyBlue.copy(alpha = 0.3f)
                else DestinyLightGray,
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (socket.plugName != null) {
            Text(
                text = "🔧",
                fontSize = 24.sp
            )
        } else {
            Text(
                text = "○",
                fontSize = 20.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun AdditionalInfoSection(item: ItemDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DestinyMediumGray
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoRow("Item Hash", item.itemHash.toString())
            InfoRow("Instance ID", item.itemInstanceId.takeLast(8))
            InfoRow("Tier", item.tierType.name)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = Color.LightGray,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun getRarityColor(tierType: TierType): Color {
    return when (tierType) {
        TierType.COMMON -> CommonWhite
        TierType.UNCOMMON -> UncommonGreen
        TierType.RARE -> RareBlue
        TierType.LEGENDARY -> LegendaryPurple
        TierType.EXOTIC -> ExoticGold
    }
}

private fun getDamageTypeColor(damageType: DamageType): Color {
    return when (damageType) {
        DamageType.KINETIC -> KineticGray
        DamageType.SOLAR -> SolarOrange
        DamageType.ARC -> ArcBlue
        DamageType.VOID -> VoidPurple
        DamageType.STASIS -> StasisBlue
        DamageType.STRAND -> StrandGreen
    }
}

