package com.ktunailab.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

// Backend direkt token ve user döndürüyor, data wrapper'ı yok
data class AuthResponse(
    @SerializedName("token")
    val token: String?,

    @SerializedName("refreshToken")
    val refreshToken: String?,

    @SerializedName("expiresAt")
    val expiresAt: String?,

    @SerializedName("user")
    val user: User
)

data class User(
    @SerializedName("id")
    val id: String,

    @SerializedName("userName")
    val userName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String?,

    @SerializedName("avatarUrl")
    val avatarUrl: String?,

    @SerializedName("roles")
    val roles: List<String>
)