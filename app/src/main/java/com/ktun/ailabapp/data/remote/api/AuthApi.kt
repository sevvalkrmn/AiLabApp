package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.request.LoginRequest
import com.ktun.ailabapp.data.remote.dto.request.RegisterRequest
import com.ktun.ailabapp.data.remote.dto.response.AuthResponse
import com.ktun.ailabapp.data.remote.network.ApiConfig
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @POST(ApiConfig.Endpoints.REGISTER)
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST(ApiConfig.Endpoints.LOGIN)
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST(ApiConfig.Endpoints.LOGOUT)
    suspend fun logout(): Response<Unit>

    @GET(ApiConfig.Endpoints.GET_PROFILE)
    suspend fun getProfile(): Response<AuthResponse>
}