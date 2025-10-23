package com.ktun.ailabapp.data.remote.interceptor

import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import com.ktun.ailabapp.data.remote.network.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val preferencesManager: PreferencesManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Token gerektirmeyen endpoint'ler (Public)
        val publicEndpoints = listOf(
            ApiConfig.Endpoints.REGISTER,
            ApiConfig.Endpoints.LOGIN
        )

        val isPublicEndpoint = publicEndpoints.any {
            originalRequest.url.encodedPath.contains(it)
        }

        return if (isPublicEndpoint) {
            // Public endpoint - token eklemeden devam et
            chain.proceed(originalRequest)
        } else {
            // Private endpoint - token ekle
            val token = runBlocking {
                preferencesManager.getToken().first()
            }

            val newRequest = originalRequest.newBuilder()
                .apply {
                    if (!token.isNullOrEmpty()) {
                        addHeader(ApiConfig.HEADER_AUTHORIZATION, "Bearer $token")
                    }
                    addHeader(ApiConfig.HEADER_CONTENT_TYPE, ApiConfig.CONTENT_TYPE_JSON)
                }
                .build()

            chain.proceed(newRequest)
        }
    }
}