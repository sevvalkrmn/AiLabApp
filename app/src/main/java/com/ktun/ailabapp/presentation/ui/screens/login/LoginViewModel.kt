package com.ktun.ailabapp.presentation.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.model.LoginUiState
import com.ktun.ailabapp.domain.usecase.auth.LoginUseCase
import com.ktun.ailabapp.domain.usecase.auth.SendPasswordResetEmailUseCase
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
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
        _uiState.value = _uiState.value.copy(rememberMe = !_uiState.value.rememberMe)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = loginUseCase(
                email = _uiState.value.email,
                password = _uiState.value.password,
                rememberMe = _uiState.value.rememberMe
            )) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        errorMessage = null
                    )
                    onSuccess()
                }
                is NetworkResult.Error -> {
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
        viewModelScope.launch {
            val result = sendPasswordResetEmailUseCase(_uiState.value.email.trim())
            if (result.isSuccess) {
                onResult(true, result.getOrDefault(""))
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Bir hata oluştu")
            }
        }
    }
}
