package com.ads.loadoutsmanager.ui.theme

import androidx.compose.runtime.*
import com.ads.loadoutsmanager.data.model.DamageType

/**
 * Theme configuration for dynamic theming based on subclass
 */
data class ThemeConfig(
    val damageType: DamageType = DamageType.KINETIC,
    val isDarkMode: Boolean = true,
    val useSubclassTheme: Boolean = false // When true, applies subclass-specific colors
)

/**
 * Theme manager for handling dynamic theme switching
 * Provides a centralized way to manage app theming
 */
class ThemeManager {
    private val _themeConfig = mutableStateOf(ThemeConfig())
    val themeConfig: State<ThemeConfig> = _themeConfig
    
    /**
     * Update theme based on selected subclass
     */
    fun setSubclassTheme(damageType: DamageType) {
        _themeConfig.value = _themeConfig.value.copy(
            damageType = damageType,
            useSubclassTheme = true
        )
    }
    
    /**
     * Reset to default theme
     */
    fun resetToDefaultTheme() {
        _themeConfig.value = _themeConfig.value.copy(
            damageType = DamageType.KINETIC,
            useSubclassTheme = false
        )
    }
    
    /**
     * Toggle dark/light mode
     */
    fun toggleDarkMode() {
        _themeConfig.value = _themeConfig.value.copy(
            isDarkMode = !_themeConfig.value.isDarkMode
        )
    }
    
    /**
     * Get primary color based on current theme configuration
     */
    fun getPrimaryColor(): androidx.compose.ui.graphics.Color {
        return if (themeConfig.value.useSubclassTheme) {
            when (themeConfig.value.damageType) {
                DamageType.SOLAR -> SubclassColors.Solar.Primary
                DamageType.ARC -> SubclassColors.Arc.Primary
                DamageType.VOID -> SubclassColors.Void.Primary
                DamageType.STASIS -> SubclassColors.Stasis.Primary
                DamageType.STRAND -> SubclassColors.Strand.Primary
                DamageType.KINETIC -> SubclassColors.Kinetic.Primary
            }
        } else {
            BaseColors.Primary
        }
    }
    
    /**
     * Get accent color based on current theme configuration
     */
    fun getAccentColor(): androidx.compose.ui.graphics.Color {
        return if (themeConfig.value.useSubclassTheme) {
            when (themeConfig.value.damageType) {
                DamageType.SOLAR -> SubclassColors.Solar.Accent
                DamageType.ARC -> SubclassColors.Arc.Accent
                DamageType.VOID -> SubclassColors.Void.Accent
                DamageType.STASIS -> SubclassColors.Stasis.Accent
                DamageType.STRAND -> SubclassColors.Strand.Accent
                DamageType.KINETIC -> SubclassColors.Kinetic.Accent
            }
        } else {
            BaseColors.Accent
        }
    }
    
    companion object {
        @Volatile
        private var instance: ThemeManager? = null
        
        fun getInstance(): ThemeManager {
            return instance ?: synchronized(this) {
                instance ?: ThemeManager().also { instance = it }
            }
        }
    }
}

/**
 * Composable to provide theme manager to the composition
 */
val LocalThemeManager = staticCompositionLocalOf { ThemeManager.getInstance() }
