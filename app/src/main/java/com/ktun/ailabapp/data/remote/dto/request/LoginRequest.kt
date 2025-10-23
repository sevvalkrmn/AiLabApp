package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("EmailOrUsername")
    val emailOrUsername: String,

    @SerializedName("password")
    val password: String
)