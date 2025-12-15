package com.ads.loadoutsmanager.data.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

/**
 * Repository for managing the Destiny 2 Manifest
 * The manifest is a SQLite database containing item definitions, perks, mods, etc.
 */
class ManifestRepository(
    private val context: Context,
    private val okHttpClient: OkHttpClient
) {
    
    companion object {
        private const val TAG = "ManifestRepository"
        private const val MANIFEST_DIR = "manifest"
        private const val MANIFEST_DB_NAME = "manifest.db"
        private const val PREFS_NAME = "manifest_prefs"
        private const val KEY_MANIFEST_VERSION = "manifest_version"
    }
    
    private val manifestDir: File by lazy {
        File(context.filesDir, MANIFEST_DIR).apply {
            if (!exists()) mkdirs()
        }
    }
    
    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Get the current manifest version stored locally
     */
    fun getCurrentManifestVersion(): String? {
        return prefs.getString(KEY_MANIFEST_VERSION, null)
    }
    
    /**
     * Download and extract the manifest database
     * @param manifestUrl URL to download the manifest zip file
     * @param version Version string to save
     */
    suspend fun downloadManifest(manifestUrl: String, version: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Downloading manifest from: $manifestUrl")
            
            // Build full URL
            val fullUrl = if (manifestUrl.startsWith("http")) {
                manifestUrl
            } else {
                "https://www.bungie.net$manifestUrl"
            }
            
            // Download the zip file
            val request = Request.Builder()
                .url(fullUrl)
                .build()
            
            val response = okHttpClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to download manifest: ${response.code}"))
            }
            
            // Extract the database from zip
            response.body?.byteStream()?.use { inputStream ->
                ZipInputStream(inputStream).use { zipStream ->
                    var entry = zipStream.nextEntry
                    
                    while (entry != null) {
                        if (!entry.isDirectory && entry.name.endsWith(".content")) {
                            // Found the database file
                            val dbFile = File(manifestDir, MANIFEST_DB_NAME)
                            
                            FileOutputStream(dbFile).use { outputStream ->
                                zipStream.copyTo(outputStream)
                            }
                            
                            // Save version
                            prefs.edit().putString(KEY_MANIFEST_VERSION, version).apply()
                            
                            Log.d(TAG, "Manifest downloaded successfully. Version: $version")
                            return@withContext Result.success(dbFile)
                        }
                        
                        entry = zipStream.nextEntry
                    }
                }
            }
            
            Result.failure(Exception("Manifest database not found in zip"))
            
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading manifest", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get the local manifest database file
     */
    fun getManifestFile(): File? {
        val dbFile = File(manifestDir, MANIFEST_DB_NAME)
        return if (dbFile.exists()) dbFile else null
    }
    
    /**
     * Check if manifest needs update
     */
    fun needsUpdate(latestVersion: String): Boolean {
        val currentVersion = getCurrentManifestVersion()
        return currentVersion == null || currentVersion != latestVersion
    }
    
    /**
     * Delete the local manifest
     */
    fun deleteManifest() {
        getManifestFile()?.delete()
        prefs.edit().remove(KEY_MANIFEST_VERSION).apply()
    }
}