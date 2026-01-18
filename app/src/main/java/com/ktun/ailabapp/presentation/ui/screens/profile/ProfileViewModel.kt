// screens/profile/ProfileViewModel.kt

package com.ktun.ailabapp.presentation.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.model.ProfileUiState // ✅ YENİ IMPORT
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.util.ImageCompressor
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.ktun.ailabapp.util.Logger
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
        loadDefaultAvatars()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = authRepository.getProfile()) {
                is NetworkResult.Success -> {
                    result.data?.let { profile ->

                        // ✅ BÜYÜK/KÜÇÜK HARF DUYARSIZ KONTROL
                        val isAdminUser = profile.roles.any { role ->
                            role.equals("admin", ignoreCase = true)
                        }

                        _uiState.value = _uiState.value.copy(
                            id = profile.id,
                            fullName = profile.fullName,
                            email = profile.email,
                            schoolNumber = profile.schoolNumber,
                            phone = profile.phone,
                            profileImageUrl = profile.profileImageUrl,
                            totalScore = profile.totalScore,
                            roles = profile.roles,
                            isAdmin = isAdminUser, // ✅ Doğru kontrol
                            isLoading = false,
                            errorMessage = null
                        )

                        Logger.d("✅ Profile loaded: ${profile.fullName}", "ProfileVM")
                        Logger.d("  - Roles: ${profile.roles}", "ProfileVM")
                        Logger.d("  - isAdmin: $isAdminUser", "ProfileVM")
                    }
                }
                is NetworkResult.Error -> {
                    Logger.e("❌ Profile error: ${result.message}", tag = "ProfileVM")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun refreshProfile() {
        loadUserProfile()
    }

    fun uploadAndUpdateProfileImage(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUploadingImage = true,
                errorMessage = null
            )

            Logger.d("📤 Compressing and uploading profile image...", "ProfileVM")

            try {
                val compressResult = ImageCompressor.compressToWebP(
                    context = context,
                    imageUri = imageUri
                )

                if (compressResult.isFailure) {
                    _uiState.value = _uiState.value.copy(
                        isUploadingImage = false,
                        errorMessage = "Fotoğraf işlenemedi"
                    )
                    return@launch
                }

                val optimizedUri = compressResult.getOrNull()!!
                Logger.d("✅ Image compressed to WebP successfully", "ProfileVM")

                val userId = _uiState.value.id
                when (val result = authRepository.uploadAndUpdateProfileImage(userId, optimizedUri)) {
                    is NetworkResult.Success -> {
                        Logger.d("✅ Profile image uploaded successfully", "ProfileVM")

                        result.data?.let { profile ->
                            _uiState.value = _uiState.value.copy(
                                profileImageUrl = profile.profileImageUrl,
                                isUploadingImage = false,
                                errorMessage = null
                            )
                        }

                        loadUserProfile()
                    }
                    is NetworkResult.Error -> {
                        Logger.e("❌ Profile image upload error: ${result.message}", tag = "ProfileVM")
                        _uiState.value = _uiState.value.copy(
                            isUploadingImage = false,
                            errorMessage = result.message ?: "Fotoğraf yüklenemedi"
                        )
                    }
                    is NetworkResult.Loading -> {}
                }
            } catch (e: Exception) {
                Logger.e("❌ Error processing image: ${e.message}", e, "ProfileVM")
                _uiState.value = _uiState.value.copy(
                    isUploadingImage = false,
                    errorMessage = "Fotoğraf işlenirken hata oluştu"
                )
            }
        }
    }

    fun selectDefaultAvatar(avatarUrl: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUploadingImage = true,
                errorMessage = null
            )

            Logger.d("🖼️ Selecting default avatar: $avatarUrl", "ProfileVM")

            when (val result = authRepository.selectDefaultAvatar(avatarUrl)) {
                is NetworkResult.Success -> {
                    Logger.d("✅ Default avatar selected successfully", "ProfileVM")

                    result.data?.let { profile ->
                        _uiState.value = _uiState.value.copy(
                            profileImageUrl = profile.profileImageUrl,
                            isUploadingImage = false,
                            errorMessage = null
                        )
                    }

                    loadUserProfile()
                }
                is NetworkResult.Error -> {
                    Logger.e("❌ Default avatar selection error: ${result.message}", tag = "ProfileVM")
                    _uiState.value = _uiState.value.copy(
                        isUploadingImage = false,
                        errorMessage = result.message ?: "Avatar seçimi başarısız"
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun loadDefaultAvatars() {
        viewModelScope.launch {
            Logger.d("📥 Loading default avatars...", "ProfileVM")

            when (val result = authRepository.getDefaultAvatars()) {
                is NetworkResult.Success -> {
                    result.data?.let { avatars ->
                        _uiState.value = _uiState.value.copy(
                            defaultAvatars = avatars
                        )
                        Logger.d("✅ Default avatars loaded: ${avatars.size}", "ProfileVM")
                    }
                }
                is NetworkResult.Error -> {
                    Logger.e("❌ Error loading default avatars: ${result.message}", tag = "ProfileVM")
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                Logger.d("🚪 Logout started", "ProfileVM")
                authRepository.logout()
                Logger.d("✅ Logout successful", "ProfileVM")

                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                Logger.e("❌ Logout error: ${e.message}", e, "ProfileVM")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Çıkış yapılırken hata oluştu"
                )
            }
        }
    }
}