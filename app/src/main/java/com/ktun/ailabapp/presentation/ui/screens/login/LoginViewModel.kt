package com.ktun.ailabapp.presentation.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.model.LoginUiState
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application.applicationContext)

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

            android.util.Log.d("LoginViewModel", """
                Login Attempt:
                Email: $email
                Password Length: ${password.length}
            """.trimIndent())

            when (val result = authRepository.login(email, password)) {
                is NetworkResult.Success -> {
                    android.util.Log.d("LoginViewModel", "Login Success!")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        errorMessage = null
                    )
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("LoginViewModel", "Login Error: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        errorMessage = result.message ?: "Giriş başarısız"
                    )
                }
                is NetworkResult.Loading -> {
                    android.util.Log.d("LoginViewModel", "Login Loading...")
                }
            }
        }
    }
}