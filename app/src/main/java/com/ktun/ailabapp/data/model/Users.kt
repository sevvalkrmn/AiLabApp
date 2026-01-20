// data/model/User.kt

package com.ktun.ailabapp.data.model

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String? = null,
    val studentNumber: String? = null,
    val username: String? = null,
    val profileImageUrl: String? = null,
    val isActive: Boolean = true,
    val points: Double? = 0.0,
    val lastLabEntry: String? = null,
    val roles: List<String>? = null,
    val projects: List<UserProject>? = null
)

// ✅ User'ın proje bilgisi
data class UserProject(
    val id: String,
    val name: String,
    val role: String? = null // "Captain" veya "Member"
)