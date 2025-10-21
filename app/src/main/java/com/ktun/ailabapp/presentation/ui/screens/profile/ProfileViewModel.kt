package com.ktun.ailabapp.presentation.ui.screens.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState(
    val name: String = "Şevval Karaman",
    val email: String = "sevvalkrmn14@gmail.com",
    val points: Int = 270,
    val profileImageUrl: String? = "https://i.pravatar.cc/300?img=1",
    val isLoading: Boolean = false
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        // TODO: API'den kullanıcı bilgilerini çek
        // Şimdilik mock data kullanıyoruz
        _uiState.update {
            it.copy(
                name = "Şevval Karaman",
                email = "sevvalkrmn14@gmail.com",
                points = 270,
                profileImageUrl = "https://i.pravatar.cc/300?img=1"
            )
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        // TODO: Logout işlemleri (token temizleme vs.)
        onLogoutSuccess()
    }
}
