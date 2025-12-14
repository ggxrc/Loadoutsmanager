package com.ads.loadoutsmanager.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
sealed class LoadoutTheme(val colorScheme: ColorScheme) {
    data object Default : LoadoutTheme(
        darkColorScheme(
            primary = SciFiCyan,
            onPrimary = Color.Black,
            primaryContainer = SciFiCyanDark,
            onPrimaryContainer = TextPrimary,
            secondary = SciFiPurple,
            onSecondary = Color.White,
            secondaryContainer = SciFiBlue,
            onSecondaryContainer = TextPrimary,
            background = SciFiDarkBackground,
            onBackground = TextPrimary,
            surface = SciFiDarkSurface,
            onSurface = TextPrimary,
            surfaceVariant = SciFiDarkSurfaceVariant,
            onSurfaceVariant = TextSecondary,
            outline = BorderColor,
            error = Color(0xFFCF6679),
            onError = Color.Black
        )
    )
    
    data object Solar : LoadoutTheme(
        darkColorScheme(
            primary = SolarOrange,
            onPrimary = Color.Black,
            primaryContainer = Color(0xFFBF360C),
            onPrimaryContainer = TextPrimary,
            secondary = Color(0xFFFFAB40),
            onSecondary = Color.Black,
            background = SciFiDarkBackground,
            onBackground = TextPrimary,
            surface = SciFiDarkSurface,
            onSurface = TextPrimary,
            surfaceVariant = Color(0xFF2D1B13),
            onSurfaceVariant = TextSecondary,
            outline = BorderColor
        )
    )
    
    data object Arc : LoadoutTheme(
        darkColorScheme(
            primary = ArcBlue,
            onPrimary = Color.Black,
            primaryContainer = Color(0xFF1565C0),
            onPrimaryContainer = TextPrimary,
            secondary = Color(0xFF40C4FF),
            onSecondary = Color.Black,
            background = SciFiDarkBackground,
            onBackground = TextPrimary,
            surface = SciFiDarkSurface,
            onSurface = TextPrimary,
            surfaceVariant = Color(0xFF1A2332),
            onSurfaceVariant = TextSecondary,
            outline = BorderColor
        )
    )
    
    data object Void : LoadoutTheme(
        darkColorScheme(
            primary = VoidPurple,
            onPrimary = Color.White,
            primaryContainer = Color(0xFF6A1B9A),
            onPrimaryContainer = TextPrimary,
            secondary = Color(0xFFB388FF),
            onSecondary = Color.Black,
            background = SciFiDarkBackground,
            onBackground = TextPrimary,
            surface = SciFiDarkSurface,
            onSurface = TextPrimary,
            surfaceVariant = Color(0xFF251A2D),
            onSurfaceVariant = TextSecondary,
            outline = BorderColor
        )
    )
    
    data object Stasis : LoadoutTheme(
        darkColorScheme(
            primary = StasisCyan,
            onPrimary = Color.Black,
            primaryContainer = Color(0xFF00838F),
            onPrimaryContainer = TextPrimary,
            secondary = Color(0xFF80DEEA),
            onSecondary = Color.Black,
            background = SciFiDarkBackground,
            onBackground = TextPrimary,
            surface = SciFiDarkSurface,
            onSurface = TextPrimary,
            surfaceVariant = Color(0xFF1A2A2D),
            onSurfaceVariant = TextSecondary,
            outline = BorderColor
        )
    )
    
    data object Strand : LoadoutTheme(
        darkColorScheme(
            primary = StrandGreen,
            onPrimary = Color.Black,
            primaryContainer = Color(0xFF00C853),
            onPrimaryContainer = TextPrimary,
            secondary = Color(0xFF69F0AE),
            onSecondary = Color.Black,
            background = SciFiDarkBackground,
            onBackground = TextPrimary,
            surface = SciFiDarkSurface,
            onSurface = TextPrimary,
            surfaceVariant = Color(0xFF1A2D1F),
            onSurfaceVariant = TextSecondary,
            outline = BorderColor
        )
    )
}

@Composable
fun getThemeForSubclass(subclassHash: Long?): LoadoutTheme {
    return when {
        subclassHash == null -> LoadoutTheme.Default
        // TODO: Map actual subclass hashes from Bungie API
        // For now, returning default theme
        else -> LoadoutTheme.Default
    }
}
