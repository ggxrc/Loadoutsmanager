package com.ads.loadoutsmanager.data.model

/**
 * Represents cosmetic customizations for an item (ornament and shader)
 * Tonalizadores = Shaders in Destiny 2
 */
data class ItemCosmetics(
    val ornamentHash: Long? = null, // Weapon/Armor ornament (skin)
    val shaderHash: Long? = null    // Shader (tonalizador) applied to the item
)
