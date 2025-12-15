package com.ads.loadoutsmanager.data.model

/**
 * Extended item data with stats and details for display
 */
data class ItemDetails(
    val itemInstanceId: String,
    val itemHash: Long,
    val name: String,
    val description: String,
    val icon: String,
    val screenshot: String?,
    val damageType: DamageType,
    val power: Int,
    val tierType: TierType,
    val itemType: String,
    val itemSubType: String,
    val stats: Map<String, Int>, // Stat name to value
    val sockets: List<SocketInfo>?,
    val perks: List<PerkInfo>?
)

data class SocketInfo(
    val socketIndex: Int,
    val plugHash: Long?,
    val plugName: String?,
    val plugIcon: String?
)

data class PerkInfo(
    val perkHash: Long,
    val perkName: String,
    val perkIcon: String,
    val perkDescription: String
)

