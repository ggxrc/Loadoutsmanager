package com.ads.loadoutsmanager.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ads.loadoutsmanager.data.model.DestinyItem
import com.ads.loadoutsmanager.ui.theme.UIColors

/**
 * Square item card component for displaying Destiny items
 * Shows item image with a border, expands to show details on click
 */
@Composable
fun ItemCard(
    item: DestinyItem,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val borderColor = when {
        isSelected -> UIColors.BorderSelected
        else -> UIColors.BorderNormal
    }
    
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                BorderStroke(2.dp, borderColor),
                RoundedCornerShape(8.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Load item icon from Bungie CDN
        if (item.iconUrl != null) {
            AsyncImage(
                model = "https://www.bungie.net${item.iconUrl}",
                contentDescription = "Item icon",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Fallback placeholder
            Text(
                text = "ITEM",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        // Show cosmetics indicator if item has ornament or shader
        if (item.cosmetics != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
            )
        }
    }
}

/**
 * Item detail sheet showing full item information
 * Displays perks, stats, and cosmetics
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailSheet(
    item: DestinyItem,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Item header
            Text(
                text = "Item Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Item image (larger version)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                        RoundedCornerShape(12.dp)
                    )
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                if (item.iconUrl != null) {
                    AsyncImage(
                        model = "https://www.bungie.net${item.iconUrl}",
                        contentDescription = "Item icon",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "ITEM",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Item hash
            DetailRow(
                label = "Item Hash",
                value = item.itemHash.toString()
            )
            
            // Bucket hash
            DetailRow(
                label = "Bucket",
                value = item.bucketHash.toString()
            )
            
            // Location
            DetailRow(
                label = "Location",
                value = item.location.name
            )
            
            // Cosmetics section
            if (item.cosmetics != null) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Cosmetics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                item.cosmetics.ornamentHash?.let { ornamentHash ->
                    DetailRow(
                        label = "Ornament",
                        value = ornamentHash.toString()
                    )
                }
                
                item.cosmetics.shaderHash?.let { shaderHash ->
                    DetailRow(
                        label = "Shader",
                        value = shaderHash.toString()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // TODO: Add perks and stats display
            Text(
                text = "Perks and stats will be displayed here once manifest data is integrated",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Close button
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Close")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Helper composable for detail rows
 */
@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
