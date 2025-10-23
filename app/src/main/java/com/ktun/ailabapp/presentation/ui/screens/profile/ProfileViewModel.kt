package com.ktun.ailabapp.presentation.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "Kullanıcı Adı",
    val email: String = "user@example.com",
    val profileImageUrl: String = "",
    val points: Int = 0,
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
        // TODO: PreferencesManager'dan kullanıcı bilgilerini yükle
        viewModelScope.launch {
            // Örnek:
            // val userData = preferencesManager.getUserData()
            // _uiState.value = _uiState.value.copy(
            //     name = userData.name,
            //     email = userData.email,
            //     points = userData.points
            // )
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