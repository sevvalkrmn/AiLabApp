package com.ktun.ailabapp.presentation.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "",  // ← Kullanıcı adı eklendi
    val greeting: String = "Good Morning",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
        updateGreeting()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            when (val result = authRepository.getProfile()) {
                is NetworkResult.Success -> {
                    result.data?.let { profile ->
                        val firstName = profile.fullName.split(" ").firstOrNull() ?: "Kullanıcı"

                        _uiState.value = _uiState.value.copy(
                            userName = firstName,
                            isLoading = false
                        )

                        android.util.Log.d("HomeViewModel", "User loaded: $firstName")
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("HomeViewModel", "Profile error: ${result.message}")

                    // Eğer token expired ise yenilemeyi dene
                    if (result.message?.contains("Oturum süresi") == true) {
                        android.util.Log.d("HomeViewModel", "Token expired, yenileme deneniyor...")

                        when (val refreshResult = authRepository.refreshToken()) {
                            is NetworkResult.Success -> {
                                android.util.Log.d("HomeViewModel", "Token yenilendi, profil tekrar yükleniyor...")
                                // Token yenilendi, profili tekrar yükle
                                loadUserProfile()
                            }
                            is NetworkResult.Error -> {
                                android.util.Log.e("HomeViewModel", "Token yenileme başarısız: ${refreshResult.message}")
                                _uiState.value = _uiState.value.copy(
                                    userName = "Kullanıcı",
                                    isLoading = false,
                                    errorMessage = "Lütfen tekrar giriş yapın"
                                )
                            }
                            is NetworkResult.Loading -> {}
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            userName = "Kullanıcı",
                            isLoading = false
                        )
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun updateGreeting() {
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when (currentHour) {
            in 6..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            in 18..21 -> "Good Evening"
            else -> "Good Night"
        }
        _uiState.value = _uiState.value.copy(greeting = greeting)
    }
}