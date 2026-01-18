// data/remote/dto/response/ProfileResponse.kt

package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    val id: String,
    val email: String,
    @SerializedName("schoolNumber")
    val schoolNumber: String,
    @SerializedName("fullName")
    val fullName: String,
    val phone: String,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String?,
    @SerializedName("totalScore")
    val totalScore: Int,
    val status: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    @SerializedName("roles") // ✅ YENİ FIELD EKLE
    val roles: List<String> = emptyList()
)