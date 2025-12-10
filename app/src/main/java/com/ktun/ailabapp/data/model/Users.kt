package com.ktun.ailabapp.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("studentNumber")
    val studentNumber: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("department")
    val department: String? = null,

    @SerializedName("grade")
    val grade: Int? = null,

    @SerializedName("role")
    val role: String? = null,


    @SerializedName("profileImageUrl")
    val profileImageUrl: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null
)
