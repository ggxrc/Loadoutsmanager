package com.ads.loadoutsmanager.data.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp Interceptor to add OAuth2 Authorization header to requests that require authentication
 */
class BungieAuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Only add auth header if we have a token
        val token = tokenProvider()
        
        val requestBuilder = originalRequest.newBuilder()
        
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}