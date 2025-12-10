package com.ktun.ailabapp.presentation.ui.screens.profile

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.util.ImageCompressor
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val schoolNumber: String = "",
    val phone: String = "",
    val profileImageUrl: String? = null, // ✅ Sadece profileImageUrl
    val totalScore: Int = 0,
    val roles: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isUploadingImage: Boolean = false,
    val defaultAvatars: List<String> = emptyList()
)

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
                        _uiState.value = _uiState.value.copy(
                            id = profile.id,
                            fullName = profile.fullName,
                            email = profile.email,
                            schoolNumber = profile.schoolNumber,
                            phone = profile.phone,
                            profileImageUrl = profile.profileImageUrl, // ✅ Sadece profileImageUrl
                            totalScore = profile.totalScore,
                            roles = profile.roles,
                            isLoading = false,
                            errorMessage = null
                        )

                        android.util.Log.d("ProfileViewModel", "Profile loaded: ${profile.fullName}")
                    } ?: run {
                        android.util.Log.e("ProfileViewModel", "Profile data is null")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Profil bilgileri alınamadı"
                        )
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("ProfileViewModel", "Profile error: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // Loading durumu zaten set edildi
                }
            }
        }
    }

    fun refreshProfile() {
        loadUserProfile()
    }

    // ✅ YENİ: Kullanıcının kendi fotoğrafını yükle
    fun uploadAndUpdateProfileImage(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUploadingImage = true,
                errorMessage = null
            )

            android.util.Log.d("ProfileViewModel", "Compressing and uploading profile image...")

            try {
                // ✅ Context parametre olarak geldi
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
                android.util.Log.d("ProfileViewModel", "Image compressed to WebP successfully")

                // Firebase'e yükle
                val userId = _uiState.value.id
                when (val result = authRepository.uploadAndUpdateProfileImage(userId, optimizedUri)) {
                    is NetworkResult.Success -> {
                        android.util.Log.d("ProfileViewModel", "Profile image uploaded successfully")

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
                        android.util.Log.e("ProfileViewModel", "Profile image upload error: ${result.message}")
                        _uiState.value = _uiState.value.copy(
                            isUploadingImage = false,
                            errorMessage = result.message ?: "Fotoğraf yüklenemedi"
                        )
                    }
                    is NetworkResult.Loading -> {}
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "Error processing image", e)
                _uiState.value = _uiState.value.copy(
                    isUploadingImage = false,
                    errorMessage = "Fotoğraf işlenirken hata oluştu"
                )
            }
        }
    }

    // ✅ YENİ: Hazır avatar seç
    fun selectDefaultAvatar(avatarUrl: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUploadingImage = true,
                errorMessage = null
            )

            android.util.Log.d("ProfileViewModel", "Selecting default avatar: $avatarUrl")

            when (val result = authRepository.selectDefaultAvatar(avatarUrl)) {
                is NetworkResult.Success -> {
                    android.util.Log.d("ProfileViewModel", "Default avatar selected successfully")

                    result.data?.let { profile ->
                        _uiState.value = _uiState.value.copy(
                            profileImageUrl = profile.profileImageUrl,
                            isUploadingImage = false,
                            errorMessage = null
                        )
                    }

                    // ✅ EKLE: Profili yeniden yükle
                    loadUserProfile()
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("ProfileViewModel", "Default avatar selection error: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        isUploadingImage = false,
                        errorMessage = result.message ?: "Avatar seçimi başarısız"
                    )
                }
                is NetworkResult.Loading -> {
                    // Loading durumu zaten set edildi
                }
            }
        }
    }

    // ✅ YENİ: Hazır avatarları yükle
    private fun loadDefaultAvatars() {
        viewModelScope.launch {
            android.util.Log.d("ProfileViewModel", "Loading default avatars...")

            when (val result = authRepository.getDefaultAvatars()) {
                is NetworkResult.Success -> {
                    result.data?.let { avatars ->
                        _uiState.value = _uiState.value.copy(
                            defaultAvatars = avatars
                        )
                        android.util.Log.d("ProfileViewModel", "Default avatars loaded: ${avatars.size}")
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("ProfileViewModel", "Error loading default avatars: ${result.message}")
                    // Hata olsa bile UI'ı bloklama, sadece log'la
                }
                is NetworkResult.Loading -> {
                    // Loading state
                }
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                android.util.Log.d("ProfileViewModel", "Logout started")
                authRepository.logout()
                android.util.Log.d("ProfileViewModel", "Logout successful")

                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "Logout error", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Çıkış yapılırken hata oluştu"
                )
            }
        }
    }
}