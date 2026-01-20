
package com.ktun.ailabapp.data.model

data class ProfileUiState(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val schoolNumber: String = "",
    val phone: String = "",
    val profileImageUrl: String? = null,
    val totalScore: Double = 0.0,
    val roles: List<String> = emptyList(),
    val isAdmin: Boolean = false, // ✅ Admin kontrolü için
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isUploadingImage: Boolean = false,
    val defaultAvatars: List<String> = emptyList()
)