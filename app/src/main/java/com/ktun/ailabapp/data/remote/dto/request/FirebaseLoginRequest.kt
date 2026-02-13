package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class FirebaseLoginRequest(
    @SerializedName("idToken")
    val idToken: String,

    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("userName")
    val userName: String? = null,

    @SerializedName("schoolNumber")
    val schoolNumber: String? = null,

    @SerializedName("phoneNumber")
    val phoneNumber: String? = null
)
