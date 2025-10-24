package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.request.LoginRequest
import com.ktun.ailabapp.data.remote.dto.request.RegisterRequest
import com.ktun.ailabapp.data.remote.dto.request.RefreshTokenRequest
import com.ktun.ailabapp.data.remote.dto.response.AuthResponse
import com.ktun.ailabapp.data.remote.dto.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("api/Auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>

    @GET("api/Profile")
    suspend fun getProfile(): Response<ProfileResponse>
}