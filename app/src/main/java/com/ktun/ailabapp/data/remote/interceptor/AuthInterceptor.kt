package com.ktunailab.ailabapp.data.remote.interceptor

import com.ktunailab.ailabapp.data.remote.network.ApiConfig
import com.ktunailab.ailabapp.data.local.datastore.PreferencesManager
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
            ApiConfig.Endpoints.LOGIN,
            ApiConfig.Endpoints.REFRESH_TOKEN
        )

        val isPublicEndpoint = publicEndpoints.any {
            originalRequest.url.encodedPath.contains(it)
        }

        // DEBUG LOG
        android.util.Log.d("AuthInterceptor", "Request URL: ${originalRequest.url}")
        android.util.Log.d("AuthInterceptor", "Is Public: $isPublicEndpoint")

        return if (isPublicEndpoint) {
            // Public endpoint - token eklemeden devam et
            android.util.Log.d("AuthInterceptor", "Public endpoint - No token added")
            chain.proceed(originalRequest)
        } else {
            // Private endpoint - token ekle
            val token = runBlocking {
                preferencesManager.getToken().first()
            }

            android.util.Log.d("AuthInterceptor", "Token: ${if (token.isNullOrEmpty()) "YOK" else token.take(20) + "..."}")

            val newRequest = originalRequest.newBuilder()
                .apply {
                    if (!token.isNullOrEmpty()) {
                        addHeader(ApiConfig.HEADER_AUTHORIZATION, "Bearer $token")
                        android.util.Log.d("AuthInterceptor", "Token header eklendi")
                    } else {
                        android.util.Log.e("AuthInterceptor", "Token YOK - Header eklenemedi!")
                    }
                    addHeader(ApiConfig.HEADER_CONTENT_TYPE, ApiConfig.CONTENT_TYPE_JSON)
                }
                .build()

            chain.proceed(newRequest)
        }
    }
}
