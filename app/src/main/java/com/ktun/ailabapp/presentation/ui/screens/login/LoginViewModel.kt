package com.ktun.ailabapp.presentation.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.model.LoginUiState
import com.ktun.ailabapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

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

    fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = authRepository.login(
                email = _uiState.value.email,
                password = _uiState.value.password
            )

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
                // Login başarılı - navigation yapılacak
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message
                )
            }
        }
    }
}

