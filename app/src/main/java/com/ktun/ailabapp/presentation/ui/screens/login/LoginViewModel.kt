package com.ktunailab.ailabapp.presentation.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktunailab.ailabapp.data.model.LoginUiState
import com.ktunailab.ailabapp.data.repository.AuthRepository
import com.ktunailab.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
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