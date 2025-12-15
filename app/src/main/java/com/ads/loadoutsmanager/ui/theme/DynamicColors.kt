package com.ads.loadoutsmanager.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Base dark sci-fi theme colors
 */
object BaseColors {
    // Background colors
    val Background = Color(0xFF0A0E27)
    val Surface = Color(0xFF151B3D)
    val SurfaceVariant = Color(0xFF1F2847)
    
    // Primary colors - tech blue
    val Primary = Color(0xFF00D9FF)
    val PrimaryVariant = Color(0xFF0099CC)
    val OnPrimary = Color(0xFF000814)
    
    // Secondary colors
    val Secondary = Color(0xFF6C63FF)
    val SecondaryVariant = Color(0xFF4A42CC)
    val OnSecondary = Color(0xFFFFFFFF)
    
    // Accent
    val Accent = Color(0xFF00FFA3)
    
    // Text colors
    val OnBackground = Color(0xFFE4E9F2)
    val OnSurface = Color(0xFFCFD8E3)
    
    // Error
    val Error = Color(0xFFFF3864)
}

/**
 * Subclass-based dynamic theme colors
 * Each subclass has its own color scheme
 */
object SubclassColors {
    
    // Solar - Orange/Fire theme
    object Solar {
        val Primary = Color(0xFFFF7200)
        val PrimaryVariant = Color(0xFFCC5C00)
        val Secondary = Color(0xFFFFAA33)
        val Accent = Color(0xFFFF4500)
        val Glow = Color(0xFFFFA726)
    }
    
    // Arc - Blue/Electric theme
    object Arc {
        val Primary = Color(0xFF00D9FF)
        val PrimaryVariant = Color(0xFF0099CC)
        val Secondary = Color(0xFF33CCFF)
        val Accent = Color(0xFF00BFFF)
        val Glow = Color(0xFF64B5F6)
    }
    
    // Void - Purple/Void theme
    object Void {
        val Primary = Color(0xFF9D4EDD)
        val PrimaryVariant = Color(0xFF7B2CBF)
        val Secondary = Color(0xFFC77DFF)
        val Accent = Color(0xFFAA00FF)
        val Glow = Color(0xFFBA68C8)
    }
    
    // Stasis - Ice/Crystal theme
    object Stasis {
        val Primary = Color(0xFF4DD0E1)
        val PrimaryVariant = Color(0xFF26C6DA)
        val Secondary = Color(0xFF80DEEA)
        val Accent = Color(0xFF00E5FF)
        val Glow = Color(0xFF4FC3F7)
    }
    
    // Strand - Green/Weave theme
    object Strand {
        val Primary = Color(0xFF00FF88)
        val PrimaryVariant = Color(0xFF00CC66)
        val Secondary = Color(0xFF66FFAA)
        val Accent = Color(0xFF00FF99)
        val Glow = Color(0xFF66BB6A)
    }
    
    // Kinetic - Gray/Neutral theme
    object Kinetic {
        val Primary = Color(0xFFB0BEC5)
        val PrimaryVariant = Color(0xFF90A4AE)
        val Secondary = Color(0xFFCFD8DC)
        val Accent = Color(0xFFECEFF1)
        val Glow = Color(0xFF9E9E9E)
    }
}

/**
 * Additional UI colors
 */
object UIColors {
    // Item rarity colors
    val Common = Color(0xFF5F7161)
    val Uncommon = Color(0xFF366B3F)
    val Rare = Color(0xFF5076A3)
    val Legendary = Color(0xFF522398)
    val Exotic = Color(0xFFCEAE33)
    
    // Status colors
    val Success = Color(0xFF00FFA3)
    val Warning = Color(0xFFFFB700)
    val Error = Color(0xFFFF3864)
    val Info = Color(0xFF00D9FF)
    
    // Item border/glow
    val BorderNormal = Color(0xFF2A3F5F)
    val BorderHover = Color(0xFF4A6FA5)
    val BorderSelected = Color(0xFF00D9FF)
}
