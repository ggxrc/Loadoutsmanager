package com.ads.loadoutsmanager.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ads.loadoutsmanager.data.model.DestinyItem
import com.ads.loadoutsmanager.ui.theme.BorderColor
import com.ads.loadoutsmanager.ui.theme.SciFiDarkSurfaceVariant

@Composable
fun ItemCard(
    item: DestinyItem,
    modifier: Modifier = Modifier,
    itemIconUrl: String? = null,
    itemName: String = "Unknown Item"
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(
            containerColor = SciFiDarkSurfaceVariant
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Main content - always visible
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.Black.copy(alpha = 0.3f))
                ) {
                    // Icon will be loaded from Bungie CDN via Coil
                    // For now, just showing placeholder
                }
                
                // Item Name
                Text(
                    text = itemName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            // Expandable details
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ItemDetails(item = item)
            }
        }
    }
}

@Composable
private fun ItemDetails(item: DestinyItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HorizontalDivider(color = BorderColor)
        
        // Stats Section
        if (item.stats.isNotEmpty()) {
            Text(
                text = "Stats",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item.stats.forEach { (statName, statValue) ->
                    StatRow(statName = statName, statValue = statValue)
                }
            }
        }
        
        // Perks Section
        if (item.perks.isNotEmpty()) {
            Text(
                text = "Perks",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Text(
                text = "${item.perks.size} active perks",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Cosmetics Section
        item.cosmetics?.let { cosmetics ->
            Text(
                text = "Cosmetics",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                cosmetics.ornamentHash?.let {
                    DetailRow(label = "Ornament", value = "Equipped")
                }
                cosmetics.shaderHash?.let {
                    DetailRow(label = "Shader", value = "Equipped")
                }
            }
        }
        
        // Item Info
        Text(
            text = "Item Info",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        DetailRow(label = "Instance ID", value = item.itemInstanceId.take(8))
        DetailRow(label = "Location", value = item.location.name)
    }
}

@Composable
private fun StatRow(statName: String, statValue: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = statName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = { statValue / 100f },
                modifier = Modifier.width(100.dp).height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.White.copy(alpha = 0.1f),
            )
            
            Text(
                text = statValue.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}
