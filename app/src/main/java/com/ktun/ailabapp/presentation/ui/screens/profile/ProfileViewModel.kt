package com.ktunailab.ailabapp.presentation.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktunailab.ailabapp.data.repository.AuthRepository
import com.ktunailab.ailabapp.util.NetworkResult
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
    val avatarUrl: String? = null, // ← null başlasın
    val totalScore: Int = 0,
    val roles: List<String> = emptyList(),
    val isLoading: Boolean = true, // ← true olarak başlasın (yükleniyor göstergesi)
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
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
                            avatarUrl = profile.avatarUrl,
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

    // ✅ YENİ: Avatar güncelleme fonksiyonu
    fun updateAvatar(avatarId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            android.util.Log.d("ProfileViewModel", "Updating avatar to: $avatarId")

            when (val result = authRepository.updateAvatar(avatarId)) {
                is NetworkResult.Success -> {
                    android.util.Log.d("ProfileViewModel", "Avatar updated successfully")

                    // Profili yeniden yükle
                    loadUserProfile()
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("ProfileViewModel", "Avatar update error: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message ?: "Avatar güncellenemedi"
                    )
                }
                is NetworkResult.Loading -> {
                    // Loading durumu zaten set edildi
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