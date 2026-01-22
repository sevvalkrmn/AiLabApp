package com.ktun.ailabapp.presentation.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.util.FirebaseAuthManager
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val step: Int = 1, // 1: Email/Pass, 2: Details
    val idToken: String? = null,
    val fullName: String = "",
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
    private val authRepository: AuthRepository,
    private val authManager: FirebaseAuthManager // ✅ Inject
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // ... Update functions ...
    fun updateFullName(fullName: String) { _uiState.value = _uiState.value.copy(fullName = fullName) }
    fun updateUsername(username: String) { _uiState.value = _uiState.value.copy(username = username) }
    fun updateEmail(email: String) { _uiState.value = _uiState.value.copy(email = email) }
    fun updateSchoolNumber(schoolNumber: String) { _uiState.value = _uiState.value.copy(schoolNumber = schoolNumber) }
    fun updatePhone(phone: String) {
        val cleanedPhone = phone.filter { it.isDigit() }.removePrefix("0")
        _uiState.value = _uiState.value.copy(phone = cleanedPhone)
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

    // Step 1 Validation & Firebase Creation
    fun createFirebaseUser() {
        val state = _uiState.value
        
        // Validate Step 1
        val error = when {
            state.email.isBlank() -> "E-posta boş bırakılamaz"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches() -> "Geçerli bir e-posta adresi girin"
            state.password.isBlank() -> "Şifre boş bırakılamaz"
            state.password.length < 8 -> "Şifre en az 8 karakter olmalıdır"
            !state.password.any { it.isUpperCase() } -> "Şifre en az 1 büyük harf içermelidir"
            state.confirmPassword.isBlank() -> "Şifre tekrar boş bırakılamaz"
            state.password != state.confirmPassword -> "Şifreler eşleşmiyor"
            else -> null
        }

        if (error != null) {
            _uiState.value = _uiState.value.copy(errorMessage = error)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = authManager.signUp(state.email, state.password)
            
            result.onSuccess { token ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    idToken = token,
                    step = 2 // Move to next step
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Firebase Hatası: ${e.message}"
                )
            }
        }
    }

    // Step 2 Validation & Backend Registration
    fun completeRegistration(onSuccess: () -> Unit) {
        val state = _uiState.value
        
        // Validate Step 2
        val error = when {
            state.fullName.isBlank() -> "Ad Soyad boş bırakılamaz"
            state.username.isBlank() -> "Kullanıcı adı boş bırakılamaz"
            state.username.length < 3 -> "Kullanıcı adı en az 3 karakter olmalıdır"
            state.schoolNumber.isBlank() -> "Okul numarası boş bırakılamaz"
            state.phone.isBlank() -> "Telefon numarası boş bırakılamaz"
            state.phone.length < 10 -> "Telefon numarası 10 haneli olmalıdır"
            else -> null
        }

        if (error != null) {
            _uiState.value = _uiState.value.copy(errorMessage = error)
            return
        }

        if (state.idToken == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Oturum token'ı bulunamadı. Lütfen başa dönün.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = authRepository.completeRegistration(
                idToken = state.idToken,
                fullName = state.fullName,
                username = state.username,
                email = state.email,
                schoolNumber = state.schoolNumber,
                phone = state.phone
            )

            when (result) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegistered = true
                    )
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
