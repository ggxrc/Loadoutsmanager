package com.ads.loadoutsmanager.data.model

/**
 * Represents a Destiny 2 character
 */
data class DestinyCharacter(
    val characterId: String,
    val classType: Int, // 0: Titan, 1: Hunter, 2: Warlock
    val raceType: Int,
    val genderType: Int,
    val light: Int,
    val emblemPath: String? = null,
    val emblemBackgroundPath: String? = null
)
