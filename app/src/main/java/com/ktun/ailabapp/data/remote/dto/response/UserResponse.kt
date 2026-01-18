// data/remote/dto/response/UserResponse.kt

package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName
import com.ktun.ailabapp.data.model.User

data class UserResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("schoolNumber")
    val schoolNumber: String?,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("profileImageUrl")
    val profileImageUrl: String?,

    @SerializedName("status")
    val status: Int?,

    @SerializedName("totalScore")
    val totalScore: Double?,

    @SerializedName("roles")
    val roles: List<String>?,

    @SerializedName("createdAt")
    val createdAt: String?,

    @SerializedName("updatedAt")
    val updatedAt: String?,

    @SerializedName("username") // ✅ Backend'den geliyorsa ekle
    val username: String?,

    @SerializedName("lastLabEntry") // ✅ Backend'den geliyorsa ekle
    val lastLabEntry: String?
)

// ✅ DTO → Domain Mapping DÜZELT
fun UserResponse.toUser(): User {
    return User(
        id = id,
        fullName = fullName,
        email = email,
        phoneNumber = phone,                    // ✅ phone → phoneNumber
        studentNumber = schoolNumber,           // ✅ schoolNumber → studentNumber
        username = username,                    // ✅ YENİ
        profileImageUrl = profileImageUrl,
        isActive = status == 1,                 // ✅ status 1 ise active
        points = totalScore?.toInt() ?: 0,      // ✅ totalScore → points (Double → Int)
        lastLabEntry = lastLabEntry,            // ✅ YENİ
        roles = roles,                          // ✅ Liste olarak
        projects = null                     // ✅ YENİ
    )
}