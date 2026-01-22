package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token")
    val token: String? = null,

    @SerializedName("refreshToken")
    val refreshToken: String? = null,

    @SerializedName("expiresAt")
    val expiresAt: String? = null,

    // Backend dökümanına göre yeni düz yapı
    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("id") // Alternatif isimlendirme (eski yapıdan kalma olabilir)
    val id: String? = null,

    @SerializedName("userName")
    val userName: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,

    @SerializedName("roles")
    val roles: List<String>? = null,

    @SerializedName("authProvider")
    val authProvider: String? = null
) {
    // Helper property to get the actual ID regardless of field name
    val actualUserId: String
        get() = userId ?: id ?: ""
}
