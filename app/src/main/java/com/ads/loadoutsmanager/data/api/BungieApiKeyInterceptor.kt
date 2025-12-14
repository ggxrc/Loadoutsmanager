package com.ads.loadoutsmanager.data.api

import com.ads.loadoutsmanager.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp Interceptor to add Bungie API key header to all requests
 */
class BungieApiKeyInterceptor : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val requestWithApiKey = originalRequest.newBuilder()
            .addHeader(BungieApiService.API_KEY_HEADER, BuildConfig.BUNGIE_API_KEY)
            .build()
        
        return chain.proceed(requestWithApiKey)
    }
}