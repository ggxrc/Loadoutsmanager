package com.ads.loadoutsmanager.data.model

import com.squareup.moshi.JsonClass

/**
 * Response from User/GetMembershipsForCurrentUser endpoint
 */
@JsonClass(generateAdapter = true)
data class UserMembershipsData(
    val destinyMemberships: List<UserMembership>,
    val primaryMembershipId: String?,
    val bungieNetUser: BungieNetUser?
)

/**
 * Bungie.net user information
 */
@JsonClass(generateAdapter = true)
data class BungieNetUser(
    val membershipId: String,
    val uniqueName: String?,
    val normalizedName: String?,
    val displayName: String?,
    val profilePicture: Int?,
    val profileTheme: Int?,
    val userTitle: Int?,
    val successMessageFlags: Long?,
    val isDeleted: Boolean?,
    val about: String?,
    val firstAccess: String?,
    val lastUpdate: String?,
    val psnDisplayName: String?,
    val xboxDisplayName: String?,
    val blizzardDisplayName: String?,
    val steamDisplayName: String?,
    val stadiaDisplayName: String?,
    val twitchDisplayName: String?
)

