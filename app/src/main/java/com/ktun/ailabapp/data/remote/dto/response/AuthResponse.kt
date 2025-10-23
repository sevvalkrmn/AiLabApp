package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

// Backend'den dönen response yapısı
// Örnek JSON:
// {
//   "success": true,
//   "message": "Kayıt başarılı",
//   "data": {
//     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
//     "user": {
//       "id": "123",
//       "firstName": "Ahmet",
//       "lastName": "Yılmaz",
//       "email": "ahmet@example.com",
//       "phone": "5551234567"
//     }
//   }
// }

data class AuthResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String?,

    @SerializedName("data")
    val data: AuthData?
)

data class AuthData(
    @SerializedName("token")
    val token: String?,

    @SerializedName("refreshToken")
    val refreshToken: String? = null,

    @SerializedName("user")
    val user: UserDto
)

data class UserDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("profileImage")
    val profileImage: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null
)