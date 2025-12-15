package com.ads.loadoutsmanager.data.model

/**
 * Cross-Save membership data
 */
data class LinkedProfiles(
    val profiles: List<UserMembership>,
    val bnetMembership: UserMembership?,
    val profilesWithErrors: List<Any>?
)

data class UserMembership(
    val membershipType: Int,
    val membershipId: String,
    val displayName: String,
    val bungieGlobalDisplayName: String?,
    val bungieGlobalDisplayNameCode: Int?,
    val crossSaveOverride: Int,
    val applicableMembershipTypes: List<Int>?,
    val isPublic: Boolean,
    val membershipFlags: Int
)

/**
 * Membership types
 */
object MembershipType {
    const val NONE = 0
    const val XBOX = 1
    const val PSN = 2
    const val STEAM = 3
    const val BLIZZARD = 4
    const val STADIA = 5
    const val DEMON = 10  // Bungie.net
    const val BUNGIE_NEXT = 254  // All platforms
}

/**
 * Resolve the actual membership to use based on Cross-Save
 */
fun LinkedProfiles.getPrimaryMembership(): UserMembership? {
    // Find profile with crossSaveOverride > 0
    val crossSaveProfile = profiles.firstOrNull { it.crossSaveOverride > 0 }
    
    // If Cross-Save is active, use the cross-save override membership
    if (crossSaveProfile != null) {
        val primaryMembershipType = crossSaveProfile.crossSaveOverride
        return profiles.firstOrNull { it.membershipType == primaryMembershipType }
    }
    
    // No Cross-Save, return the first Destiny profile found
    return profiles.firstOrNull { 
        it.membershipType in listOf(
            MembershipType.XBOX,
            MembershipType.PSN,
            MembershipType.STEAM,
            MembershipType.STADIA
        )
    }
}