package com.ktun.ailabapp.presentation.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.model.LoginUiState
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.util.FirebaseAuthManager
import com.ktun.ailabapp.util.Logger
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authManager: FirebaseAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun toggleRememberMe() {
        _uiState.value = _uiState.value.copy(
            rememberMe = !_uiState.value.rememberMe
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val email = _uiState.value.email.trim()
            val password = _uiState.value.password
            val rememberMe = _uiState.value.rememberMe  // ✅ YENİ

            Logger.d("Login attempt started", "LoginViewModel")

            when (val result = authRepository.login(email, password, rememberMe)) {
                is NetworkResult.Success -> {
                    Logger.d("Login successful", "LoginViewModel")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        errorMessage = null
                    )
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    Logger.e("Login failed: ${result.message}", tag = "LoginViewModel")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        errorMessage = result.message ?: "Giriş başarısız"
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun sendPasswordResetEmail(onResult: (Boolean, String) -> Unit) {
        val email = _uiState.value.email.trim()
        if (email.isBlank()) {
            onResult(false, "Lütfen e-posta adresinizi girin")
            return
        }

        viewModelScope.launch {
            val result = authManager.sendPasswordResetEmail(email)
            if (result.isSuccess) {
                onResult(true, "Şifre sıfırlama bağlantısı e-posta adresinize gönderildi")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Bir hata oluştu")
            }
        }
    }
}