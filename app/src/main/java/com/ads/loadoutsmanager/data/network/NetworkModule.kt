package com.ads.loadoutsmanager.data.network

import com.ads.loadoutsmanager.data.api.BungieApiKeyInterceptor
import com.ads.loadoutsmanager.data.api.BungieAuthInterceptor
import com.ads.loadoutsmanager.data.api.BungieApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Network configuration and Retrofit service creation
 */
object NetworkModule {
    
    /**
     * Create Moshi instance for JSON parsing
     */
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    /**
     * Create OkHttpClient with interceptors
     * @param tokenProvider Function to provide current OAuth2 token
     */
    fun createOkHttpClient(tokenProvider: () -> String? = { null }): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(BungieApiKeyInterceptor())
            .addInterceptor(BungieAuthInterceptor(tokenProvider))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Create Retrofit instance
     */
    fun createRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BungieApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    /**
     * Create BungieApiService
     */
    fun createBungieApiService(tokenProvider: () -> String? = { null }): BungieApiService {
        val okHttpClient = createOkHttpClient(tokenProvider)
        val retrofit = createRetrofit(okHttpClient)
        return retrofit.create(BungieApiService::class.java)
    }
}