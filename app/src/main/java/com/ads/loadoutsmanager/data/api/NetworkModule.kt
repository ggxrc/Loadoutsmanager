package com.ads.loadoutsmanager.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * Network module for creating Retrofit instance and API service
 */
object NetworkModule {
    
    private const val BASE_URL = "https://www.bungie.net/Platform/"
    private const val TIMEOUT_SECONDS = 30L
    
    /**
     * Creates an OkHttpClient with OAuth2 token interceptor
     * @param apiKey Bungie API key
     * @param accessToken OAuth2 access token
     */
    fun createOkHttpClient(
        apiKey: String,
        accessToken: String? = null
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .header("X-API-Key", apiKey)
            
            // Add OAuth2 token if available
            accessToken?.let {
                requestBuilder.header("Authorization", "Bearer $it")
            }
            
            chain.proceed(requestBuilder.build())
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Creates Moshi instance for JSON parsing
     */
    fun createMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    
    /**
     * Creates Retrofit instance
     * @param okHttpClient OkHttpClient with authentication
     */
    fun createRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(createMoshi()))
            .build()
    }
    
    /**
     * Creates BungieApiService instance
     * @param apiKey Bungie API key
     * @param accessToken OAuth2 access token
     */
    fun createBungieApiService(
        apiKey: String,
        accessToken: String? = null
    ): BungieApiService {
        val client = createOkHttpClient(apiKey, accessToken)
        val retrofit = createRetrofit(client)
        return retrofit.create(BungieApiService::class.java)
    }
}
