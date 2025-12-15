package com.ads.loadoutsmanager.data.api

import retrofit2.http.GET

/**
 * API service for Destiny Manifest
 */
interface ManifestService {
    
    /**
     * Get the current manifest information
     * Returns URLs to download the manifest database
     */
    @GET("Destiny2/Manifest/")
    suspend fun getManifest(): BungieResponse<ManifestData>
}

/**
 * Manifest data containing download URLs
 */
data class ManifestData(
    val version: String,
    val mobileAssetContentPath: String,
    val mobileGearAssetDataBases: List<MobileGearAssetData>?,
    val mobileWorldContentPaths: Map<String, String>,  // Language -> URL
    val jsonWorldContentPaths: Map<String, String>,
    val jsonWorldComponentContentPaths: Map<String, Map<String, String>>,
    val mobileClanBannerDatabasePath: String?,
    val mobileGearCDN: Map<String, String>?,
    val iconImagePyramidInfo: List<ImagePyramidEntry>?
)

data class MobileGearAssetData(
    val version: Int,
    val path: String
)

data class ImagePyramidEntry(
    val name: String,
    val factor: Float
)