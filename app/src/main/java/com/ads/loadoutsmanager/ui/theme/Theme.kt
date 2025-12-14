package com.ads.loadoutsmanager.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.ads.loadoutsmanager.data.model.DamageType

/**
 * Base dark color scheme for sci-fi aesthetic
 */
private val DarkColorScheme = darkColorScheme(
    primary = BaseColors.Primary,
    onPrimary = BaseColors.OnPrimary,
    secondary = BaseColors.Secondary,
    onSecondary = BaseColors.OnSecondary,
    tertiary = BaseColors.Accent,
    background = BaseColors.Background,
    surface = BaseColors.Surface,
    surfaceVariant = BaseColors.SurfaceVariant,
    onBackground = BaseColors.OnBackground,
    onSurface = BaseColors.OnSurface,
    error = BaseColors.Error
)

/**
 * Light color scheme (for future implementation)
 */
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

/**
 * Get dynamic color scheme based on subclass damage type
 */
@Composable
private fun getSubclassColorScheme(damageType: DamageType) = darkColorScheme(
    primary = when (damageType) {
        DamageType.SOLAR -> SubclassColors.Solar.Primary
        DamageType.ARC -> SubclassColors.Arc.Primary
        DamageType.VOID -> SubclassColors.Void.Primary
        DamageType.STASIS -> SubclassColors.Stasis.Primary
        DamageType.STRAND -> SubclassColors.Strand.Primary
        DamageType.KINETIC -> SubclassColors.Kinetic.Primary
    },
    onPrimary = BaseColors.OnPrimary,
    secondary = when (damageType) {
        DamageType.SOLAR -> SubclassColors.Solar.Secondary
        DamageType.ARC -> SubclassColors.Arc.Secondary
        DamageType.VOID -> SubclassColors.Void.Secondary
        DamageType.STASIS -> SubclassColors.Stasis.Secondary
        DamageType.STRAND -> SubclassColors.Strand.Secondary
        DamageType.KINETIC -> SubclassColors.Kinetic.Secondary
    },
    onSecondary = BaseColors.OnSecondary,
    tertiary = when (damageType) {
        DamageType.SOLAR -> SubclassColors.Solar.Accent
        DamageType.ARC -> SubclassColors.Arc.Accent
        DamageType.VOID -> SubclassColors.Void.Accent
        DamageType.STASIS -> SubclassColors.Stasis.Accent
        DamageType.STRAND -> SubclassColors.Strand.Accent
        DamageType.KINETIC -> SubclassColors.Kinetic.Accent
    },
    background = BaseColors.Background,
    surface = BaseColors.Surface,
    surfaceVariant = BaseColors.SurfaceVariant,
    onBackground = BaseColors.OnBackground,
    onSurface = BaseColors.OnSurface,
    error = BaseColors.Error
)

/**
 * Main theme composable with dynamic theming support
 * Supports subclass-based theme switching and dark sci-fi aesthetic
 */
@Composable
fun LoadoutsManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled by default to use custom theme
    themeManager: ThemeManager = ThemeManager.getInstance(),
    content: @Composable () -> Unit
) {
    val themeConfig = themeManager.themeConfig.value
    
    val colorScheme = when {
        // Dynamic colors (Android 12+) - only if explicitly enabled
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        
        // Subclass-based dynamic theming
        themeConfig.useSubclassTheme -> {
            getSubclassColorScheme(themeConfig.damageType)
        }
        
        // Base dark sci-fi theme
        darkTheme -> DarkColorScheme
        
        // Light theme (fallback)
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalThemeManager provides themeManager) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}