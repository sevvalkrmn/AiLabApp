package com.ktun.ailabapp.presentation.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val schoolNumber: String = "",
    val phone: String = "",
    val avatarUrl: String? = null,
    val totalScore: Int = 0,
    val roles: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application.applicationContext)

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
                    result.data?.let { profile ->  // ← Safe call ekLendi
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
                        // data null ise
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