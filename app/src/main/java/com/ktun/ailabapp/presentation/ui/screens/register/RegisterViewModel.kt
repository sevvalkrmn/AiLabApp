package com.ktun.ailabapp.presentation.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.domain.usecase.auth.CompleteRegistrationUseCase
import com.ktun.ailabapp.domain.usecase.auth.ValidateRegistrationStep1UseCase
import com.ktun.ailabapp.domain.usecase.auth.ValidateRegistrationStep2UseCase
import com.ktun.ailabapp.util.FirebaseAuthManager
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val step: Int = 1,
    val idToken: String? = null,
    val fullName: String = "",
    val surname: String = "",
    val username: String = "",
    val email: String = "",
    val schoolNumber: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistered: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val validateStep1UseCase: ValidateRegistrationStep1UseCase,
    private val validateStep2UseCase: ValidateRegistrationStep2UseCase,
    private val completeRegistrationUseCase: CompleteRegistrationUseCase,
    private val authManager: FirebaseAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateFullName(fullName: String) { _uiState.value = _uiState.value.copy(fullName = fullName) }
    fun updateSurname(surname: String) { _uiState.value = _uiState.value.copy(surname = surname) }
    fun updateUsername(username: String) { _uiState.value = _uiState.value.copy(username = username) }
    fun updateEmail(email: String) { _uiState.value = _uiState.value.copy(email = email) }
    fun updateSchoolNumber(schoolNumber: String) { _uiState.value = _uiState.value.copy(schoolNumber = schoolNumber) }
    fun updatePhone(phone: String) {
        val cleaned = phone.filter { it.isDigit() }.removePrefix("0").take(10)
        _uiState.value = _uiState.value.copy(phone = cleaned)
    }
    fun updatePassword(password: String) { _uiState.value = _uiState.value.copy(password = password) }
    fun updateConfirmPassword(confirmPassword: String) { _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword) }
    fun togglePasswordVisibility() { _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible) }
    fun toggleConfirmPasswordVisibility() { _uiState.value = _uiState.value.copy(isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible) }

    fun previousStep() {
        if (_uiState.value.step == 2) {
            _uiState.value = _uiState.value.copy(step = 1, errorMessage = null)
        }
    }

    fun createFirebaseUser() {
        val state = _uiState.value
        val error = validateStep1UseCase(state.email, state.password, state.confirmPassword)
        if (error != null) {
            _uiState.value = state.copy(errorMessage = error)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            authManager.signUp(state.email, state.password)
                .onSuccess { token ->
                    _uiState.value = _uiState.value.copy(isLoading = false, idToken = token, step = 2)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Firebase Hatası: ${e.message}"
                    )
                }
        }
    }

    fun completeRegistration(onSuccess: () -> Unit) {
        val state = _uiState.value
        val error = validateStep2UseCase(state.fullName, state.surname, state.username, state.schoolNumber, state.phone)
        if (error != null) {
            _uiState.value = state.copy(errorMessage = error)
            return
        }
        if (state.idToken == null) {
            _uiState.value = state.copy(errorMessage = "Oturum token'ı bulunamadı. Lütfen başa dönün.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = completeRegistrationUseCase(
                idToken = state.idToken,
                fullName = state.fullName,
                surname = state.surname,
                username = state.username,
                email = state.email,
                schoolNumber = state.schoolNumber,
                phone = state.phone
            )) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, isRegistered = true)
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
