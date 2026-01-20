package com.ktun.ailabapp.data.remote.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET

interface ProfileApi {
    @GET("api/Profile/avatars/defaults")
    suspend fun getDefaultAvatars(): Response<DefaultAvatarsResponse>
}

data class DefaultAvatarsResponse(
    @SerializedName("avatarUrls") val avatarUrls: List<String>
)