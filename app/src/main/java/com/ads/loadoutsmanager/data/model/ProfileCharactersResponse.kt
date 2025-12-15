package com.ads.loadoutsmanager.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Complete profile response from Bungie API
 * Component 200: Characters
 */
@JsonClass(generateAdapter = true)
data class ProfileCharactersResponse(
    @Json(name = "characters") val characters: CharactersComponent?
)

@JsonClass(generateAdapter = true)
data class CharactersComponent(
    @Json(name = "data") val data: Map<String, DestinyCharacterData>?
)

@JsonClass(generateAdapter = true)
data class DestinyCharacterData(
    @Json(name = "membershipId") val membershipId: String,
    @Json(name = "membershipType") val membershipType: Int,
    @Json(name = "characterId") val characterId: String,
    @Json(name = "dateLastPlayed") val dateLastPlayed: String,
    @Json(name = "minutesPlayedThisSession") val minutesPlayedThisSession: String,
    @Json(name = "minutesPlayedTotal") val minutesPlayedTotal: String,
    @Json(name = "light") val light: Int,
    @Json(name = "stats") val stats: Map<String, Int>,
    @Json(name = "raceHash") val raceHash: Long,
    @Json(name = "genderHash") val genderHash: Long,
    @Json(name = "classHash") val classHash: Long,
    @Json(name = "raceType") val raceType: Int,
    @Json(name = "classType") val classType: Int, // 0: Titan, 1: Hunter, 2: Warlock
    @Json(name = "genderType") val genderType: Int,
    @Json(name = "emblemPath") val emblemPath: String,
    @Json(name = "emblemBackgroundPath") val emblemBackgroundPath: String,
    @Json(name = "emblemHash") val emblemHash: Long,
    @Json(name = "emblemColor") val emblemColor: EmblemColor?,
    @Json(name = "levelProgression") val levelProgression: Progression?,
    @Json(name = "baseCharacterLevel") val baseCharacterLevel: Int,
    @Json(name = "percentToNextLevel") val percentToNextLevel: Double
)

@JsonClass(generateAdapter = true)
data class EmblemColor(
    @Json(name = "red") val red: Int,
    @Json(name = "green") val green: Int,
    @Json(name = "blue") val blue: Int,
    @Json(name = "alpha") val alpha: Int
)

@JsonClass(generateAdapter = true)
data class Progression(
    @Json(name = "progressionHash") val progressionHash: Long,
    @Json(name = "dailyProgress") val dailyProgress: Int,
    @Json(name = "dailyLimit") val dailyLimit: Int,
    @Json(name = "weeklyProgress") val weeklyProgress: Int,
    @Json(name = "weeklyLimit") val weeklyLimit: Int,
    @Json(name = "currentProgress") val currentProgress: Int,
    @Json(name = "level") val level: Int,
    @Json(name = "levelCap") val levelCap: Int,
    @Json(name = "stepIndex") val stepIndex: Int,
    @Json(name = "progressToNextLevel") val progressToNextLevel: Int,
    @Json(name = "nextLevelAt") val nextLevelAt: Int
)

/**
 * Extension to convert to simple DestinyCharacter model
 */
fun DestinyCharacterData.toDestinyCharacter(): DestinyCharacter {
    return DestinyCharacter(
        characterId = characterId,
        classType = classType,
        raceType = raceType,
        genderType = genderType,
        light = light,
        emblemPath = emblemPath,
        emblemBackgroundPath = emblemBackgroundPath
    )
}

