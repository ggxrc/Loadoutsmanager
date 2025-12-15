package com.ads.loadoutsmanager.data.model

/**
 * User information from Bungie API GetCurrentUser endpoint
 * Only essential fields are required, most are nullable
 */
data class UserInfo(
    val membershipId: String,
    val uniqueName: String? = null,
    val normalizedName: String? = null,
    val displayName: String? = null,
    val profilePicture: Int? = null,
    val profileTheme: Int? = null,
    val userTitle: Int? = null,
    val successMessageFlags: Long? = null,
    val isDeleted: Boolean? = null,
    val about: String? = null,
    val firstAccess: String? = null,
    val lastUpdate: String? = null,
    val legacyPortalUID: Long? = null,
    val context: UserInfoContext? = null,
    val psnDisplayName: String? = null,
    val xboxDisplayName: String? = null,
    val fbDisplayName: String? = null,
    val showActivity: Boolean? = null,
    val locale: String? = null,
    val localeInheritDefault: Boolean? = null,
    val lastBanReportId: Long? = null,
    val showGroupMessaging: Boolean? = null,
    val profilePicturePath: String? = null,
    val profilePictureWidePath: String? = null,
    val profileThemeName: String? = null,
    val userTitleDisplay: String? = null,
    val statusText: String? = null,
    val statusDate: String? = null,
    val profileBanExpire: String? = null,
    val blizzardDisplayName: String? = null,
    val steamDisplayName: String? = null,
    val stadiaDisplayName: String? = null,
    val twitchDisplayName: String? = null,
    val cachedBungieGlobalDisplayName: String? = null,
    val cachedBungieGlobalDisplayNameCode: Int? = null
)

data class UserInfoContext(
    val isFollowing: Boolean,
    val ignoreStatus: UserInfoIgnoreStatus?
)

data class UserInfoIgnoreStatus(
    val isIgnored: Boolean,
    val ignoreFlags: Int
)
