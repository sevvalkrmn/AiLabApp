package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateUserImageRequest(
    @SerializedName("profileImageUrl")
    val profileImageUrl: String
)
