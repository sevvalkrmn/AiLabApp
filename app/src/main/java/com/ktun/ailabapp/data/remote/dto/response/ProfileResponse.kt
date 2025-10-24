package com.ktunailab.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("schoolNumber")
    val schoolNumber: String,

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("avatarUrl")
    val avatarUrl: String?,

    @SerializedName("status")
    val status: Int,

    @SerializedName("totalScore")
    val totalScore: Int,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("roles")
    val roles: List<String>
)