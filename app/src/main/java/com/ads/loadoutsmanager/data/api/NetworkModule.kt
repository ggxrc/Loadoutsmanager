package com.ads.loadoutsmanager.data.api

import okhttp3.Authenticator
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
 * Uses configuration from BungieConfig (loaded from local.properties)
 */
object NetworkModule {

    private const val TIMEOUT_SECONDS = 30L

    /**
     * Creates an OkHttpClient with OAuth2 token interceptor
     * @param getAccessToken Lambda function to get current access token
     * @param authenticator Optional authenticator for token refresh
     */
    fun createOkHttpClient(
        getAccessToken: () -> String? = { null },
        authenticator: Authenticator? = null
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .header("X-API-Key", BungieConfig.apiKey)

            // Add OAuth2 token if available - get it dynamically
            val token = getAccessToken()
            if (token != null) {
                requestBuilder.header("Authorization", "Bearer $token")
                android.util.Log.d("NetworkModule", "🔑 Adding Bearer token to: ${originalRequest.url}")
            } else {
                android.util.Log.w("NetworkModule", "⚠️ No token available for: ${originalRequest.url}")
            }

            val request = requestBuilder.build()
            android.util.Log.d("NetworkModule", "📤 REQUEST: ${request.method} ${request.url}")

            val response = chain.proceed(request)

            android.util.Log.d("NetworkModule", "📥 RESPONSE: ${response.code} ${request.url}")
            if (!response.isSuccessful) {
                android.util.Log.e("NetworkModule", "❌ HTTP ${response.code}: ${response.message}")
            }

            response
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .apply {
                authenticator?.let {
                    authenticator(it)
                }
            }
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
            .baseUrl(BungieConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(createMoshi()))
            .build()
    }

    /**
     * Creates BungieApiService instance
     * @param getAccessToken Lambda function to get current access token
     * @param authenticator Optional authenticator for token refresh
     */
    fun createBungieApiService(
        getAccessToken: () -> String? = { null },
        authenticator: Authenticator? = null
    ): BungieApiService {
        val client = createOkHttpClient(getAccessToken, authenticator)
        val retrofit = createRetrofit(client)
        return retrofit.create(BungieApiService::class.java)
    }

    /**
     * Creates ManifestService instance
     */
    fun createManifestService(): ManifestService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val apiKeyInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .header("X-API-Key", BungieConfig.apiKey)
                .build()
            chain.proceed(newRequest)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BungieConfig.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(createMoshi()))
            .build()

        return retrofit.create(ManifestService::class.java)
    }
}
