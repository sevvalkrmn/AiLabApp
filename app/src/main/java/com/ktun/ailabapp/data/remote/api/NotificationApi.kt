package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.request.RegisterFcmTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface NotificationApi {

    @POST("api/notifications/register-token")
    suspend fun registerFcmToken(
        @Body request: RegisterFcmTokenRequest
    ): Response<Unit>

    @DELETE("api/notifications/unregister-token")
    suspend fun unregisterFcmToken(): Response<Unit>
}
