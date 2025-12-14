package com.ads.loadoutsmanager.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * Component for loading item icons from Bungie CDN
 * Base URL: https://www.bungie.net
 */
@Composable
fun BungieItemIcon(
    iconPath: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    borderWidth: Dp = 2.dp
) {
    val fullIconUrl = iconPath?.let { "https://www.bungie.net$it" }
    
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        if (fullIconUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(fullIconUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

/**
 * Component for loading character emblems
 */
@Composable
fun BungieEmblem(
    emblemPath: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val fullEmblemUrl = emblemPath?.let { "https://www.bungie.net$it" }
    
    if (fullEmblemUrl != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(fullEmblemUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Usage example:
 * 
 * // Item icon from Bungie API response
 * BungieItemIcon(
 *     iconPath = item.iconPath,  // e.g., "/common/destiny2_content/icons/abc123.jpg"
 *     contentDescription = "Exotic Hand Cannon"
 * )
 * 
 * // Character emblem
 * BungieEmblem(
 *     emblemPath = character.emblemPath,
 *     contentDescription = "Guardian emblem",
 *     modifier = Modifier.fillMaxWidth().height(96.dp)
 * )
 */
