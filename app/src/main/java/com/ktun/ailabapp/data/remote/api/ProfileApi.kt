package com.ktun.ailabapp.data.remote.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET

import com.ktun.ailabapp.data.remote.dto.request.UpdateEmailRequest // ✅ Import added
import retrofit2.http.Body // ✅ Import added
import retrofit2.http.PUT // ✅ Import added

interface ProfileApi {
    @GET("api/Profile/avatars/defaults")
    suspend fun getDefaultAvatars(): Response<DefaultAvatarsResponse>

    @PUT("api/profile/update-email")
    suspend fun updateEmail(@Body request: UpdateEmailRequest): Response<Unit>
}

data class DefaultAvatarsResponse(
    @SerializedName("avatarUrls") val avatarUrls: List<String>
)