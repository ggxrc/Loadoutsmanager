package com.ads.loadoutsmanager.data.model

/**
 * Destiny component types for API requests
 */
object DestinyComponents {
    // Profile components
    const val PROFILES = 100
    const val VENDOR_RECEIPTS = 101
    const val PROFILE_INVENTORIES = 102
    const val PROFILE_CURRENCIES = 103
    const val PROFILE_PROGRESSION = 104
    const val PLATFORM_SILVER = 105
    const val CHARACTERS = 200
    const val CHARACTER_INVENTORIES = 201
    const val CHARACTER_PROGRESSIONS = 202
    const val CHARACTER_RENDER_DATA = 203
    const val CHARACTER_ACTIVITIES = 204
    const val CHARACTER_EQUIPMENT = 205
    
    // Item components
    const val ITEM_INSTANCES = 300
    const val ITEM_OBJECTIVES = 301
    const val ITEM_PERKS = 302
    const val ITEM_RENDER_DATA = 303
    const val ITEM_STATS = 304
    const val ITEM_SOCKETS = 305  // Critical for Mods and Perks
    const val ITEM_TALENT_GRIDS = 306
    const val ITEM_COMMON_DATA = 307
    const val ITEM_PLUG_STATES = 308
    const val ITEM_PLUG_OBJECTIVES = 309
    const val ITEM_REUSABLE_PLUGS = 310
    
    // Vendors
    const val VENDORS = 400
    const val VENDOR_CATEGORIES = 401
    const val VENDOR_SALES = 402
    
    // Misc
    const val KIOSKS = 500
    const val CURRENCY_LOOKUPS = 600
    const val PRESENTATION_NODES = 700
    const val COLLECTIBLES = 800
    const val RECORDS = 900
    const val TRANSITORY = 1000
    const val METRICS = 1100
    const val STRING_VARIABLES = 1200
    const val CRAFTABLES = 1300
    const val SOCIAL_COMMENDATIONS = 1400
    
    /**
     * Build components string for API request
     * Example: buildComponentsString(PROFILES, CHARACTERS, CHARACTER_EQUIPMENT)
     * Returns: "100,200,205"
     */
    fun buildComponentsString(vararg components: Int): String {
        return components.joinToString(",")
    }
    
    /**
     * Common component sets for loadout operations
     */
    object LoadoutComponents {
        val FULL_PROFILE = buildComponentsString(
            PROFILES,
            CHARACTERS,
            CHARACTER_INVENTORIES,
            CHARACTER_EQUIPMENT,
            PROFILE_INVENTORIES,
            ITEM_INSTANCES,
            ITEM_SOCKETS  // Critical for mods and cosmetics
        )
        
        val CHARACTER_EQUIPMENT_ONLY = buildComponentsString(
            CHARACTERS,
            CHARACTER_EQUIPMENT,
            ITEM_INSTANCES,
            ITEM_SOCKETS
        )
        
        val INVENTORY_ONLY = buildComponentsString(
            CHARACTER_INVENTORIES,
            PROFILE_INVENTORIES,
            ITEM_INSTANCES
        )
    }
}