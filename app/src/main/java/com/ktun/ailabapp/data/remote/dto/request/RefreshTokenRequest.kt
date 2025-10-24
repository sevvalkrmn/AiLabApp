package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class RefreshTokenRequest(
    @SerializedName("refreshToken")
    val refreshToken: String
)