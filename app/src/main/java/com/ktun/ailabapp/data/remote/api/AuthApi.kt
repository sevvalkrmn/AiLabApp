package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.request.FirebaseLoginRequest
import com.ktun.ailabapp.data.remote.dto.request.LoginRequest
import com.ktun.ailabapp.data.remote.dto.request.RefreshTokenRequest
import com.ktun.ailabapp.data.remote.dto.request.RegisterRequest
import com.ktun.ailabapp.data.remote.dto.request.UpdateEmailRequest
import com.ktun.ailabapp.data.remote.dto.request.UpdatePhoneRequest
import com.ktun.ailabapp.data.remote.dto.request.UpdateProfileImageRequest
import com.ktun.ailabapp.data.remote.dto.response.AuthResponse
import com.ktun.ailabapp.data.remote.dto.response.DefaultAvatarsResponse
import com.ktun.ailabapp.data.remote.dto.response.LeaderboardUserResponse
import com.ktun.ailabapp.data.remote.dto.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/Auth/login-firebase")
    suspend fun loginFirebase(@Body request: FirebaseLoginRequest): Response<AuthResponse>

    @POST("api/auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("api/Auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>

    @GET("api/Profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @PUT("api/profile/image")
    suspend fun updateProfileImage(
        @Body request: UpdateProfileImageRequest
    ): Response<ProfileResponse>

    @PUT("api/profile/update-email")
    suspend fun updateEmail(@Body request: UpdateEmailRequest): Response<Unit>

    @PUT("api/profile/update-phone")
    suspend fun updatePhone(@Body request: UpdatePhoneRequest): Response<Unit>

    @GET("api/profile/avatars/defaults")
    suspend fun getDefaultAvatars(): Response<DefaultAvatarsResponse>

    @GET("api/Profile/leaderboard")
    suspend fun getLeaderboard(): Response<List<LeaderboardUserResponse>>
}