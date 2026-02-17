package com.ktun.ailabapp.data.remote.interceptor

import com.ktun.ailabapp.data.remote.network.ApiConfig
import com.ktun.ailabapp.util.FirebaseAuthManager
import com.ktun.ailabapp.util.Logger
import com.ktun.ailabapp.util.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authManager: FirebaseAuthManager,
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Token gerektirmeyen endpoint'ler
        val publicEndpoints = listOf(
            ApiConfig.Endpoints.REGISTER,
            ApiConfig.Endpoints.LOGIN,
            ApiConfig.Endpoints.LOGIN_FIREBASE,
            ApiConfig.Endpoints.REFRESH_TOKEN
        )

        val isPublicEndpoint = publicEndpoints.any {
            originalRequest.url.encodedPath.contains(it)
        }

        if (isPublicEndpoint) {
            return chain.proceed(originalRequest)
        }

        // Cached token'ı senkron oku — runBlocking yok, thread bloke olmaz
        val token = authManager.getTokenSync()

        val newRequest = originalRequest.newBuilder()
            .apply {
                if (!token.isNullOrEmpty()) {
                    addHeader(ApiConfig.HEADER_AUTHORIZATION, "Bearer $token")
                }
                addHeader(ApiConfig.HEADER_CONTENT_TYPE, ApiConfig.CONTENT_TYPE_JSON)
            }
            .build()

        val response = chain.proceed(newRequest)

        if (response.code == 401) {
            Logger.e("401 Unauthorized - Oturum süresi doldu: ${originalRequest.url}", tag = "AuthInterceptor")
            sessionManager.onSessionExpired()
        }

        return response
    }
}
