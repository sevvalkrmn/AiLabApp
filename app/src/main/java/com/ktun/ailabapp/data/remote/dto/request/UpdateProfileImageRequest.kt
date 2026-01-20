// data/remote/dto/request/UpdateProfileImageRequest.kt

package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateProfileImageRequest(
    @SerializedName("profileImageUrl") val profileImageUrl: String
)

data class UpdateProfileImageResponse(
    @SerializedName("message") val message: String,
    @SerializedName("url") val url: String
)