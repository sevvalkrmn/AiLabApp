package com.ktun.ailabapp.presentation.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
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
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateFullName(fullName: String) {
        _uiState.value = _uiState.value.copy(fullName = fullName)
    }

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updateSchoolNumber(schoolNumber: String) {
        _uiState.value = _uiState.value.copy(schoolNumber = schoolNumber)
    }

    fun updatePhone(phone: String) {
        // Sadece rakam girişine izin ver ve başındaki 0'ı kaldır
        val cleanedPhone = phone.filter { it.isDigit() }.removePrefix("0")
        _uiState.value = _uiState.value.copy(phone = cleanedPhone)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
        )
    }

    private fun validateInputs(): String? {
        val state = _uiState.value

        return when {
            state.fullName.isBlank() -> "Ad Soyad boş bırakılamaz"
            state.username.isBlank() -> "Kullanıcı adı boş bırakılamaz"
            state.username.length < 3 -> "Kullanıcı adı en az 3 karakter olmalıdır"
            state.username.length > 50 -> "Kullanıcı adı en fazla 50 karakter olabilir"
            state.email.isBlank() -> "E-posta boş bırakılamaz"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches() -> "Geçerli bir e-posta adresi girin"
            state.schoolNumber.isBlank() -> "Okul numarası boş bırakılamaz"
            state.phone.isBlank() -> "Telefon numarası boş bırakılamaz"
            state.phone.length < 10 -> "Telefon numarası 10 haneli olmalıdır"
            state.password.isBlank() -> "Şifre boş bırakılamaz"
            state.password.length < 8 -> "Şifre en az 8 karakter olmalıdır"
            !state.password.any { it.isUpperCase() } -> "Şifre en az 1 büyük harf içermelidir"
            state.confirmPassword.isBlank() -> "Şifre tekrar boş bırakılamaz"
            state.password != state.confirmPassword -> "Şifreler eşleşmiyor"
            else -> null
        }
    }

    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Validasyonları kontrol et
            val validationError = validateInputs()
            if (validationError != null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = validationError
                )
                return@launch
            }

            // Backend'e kayıt isteği gönder
            val state = _uiState.value

            when (val result = authRepository.register(
                fullName = state.fullName,
                username = state.username,
                email = state.email,
                schoolNumber = state.schoolNumber,
                phone = state.phone,
                password = state.password
            )) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegistered = true,
                        errorMessage = null
                    )
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegistered = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // Loading durumu zaten set edildi
                }
            }
        }
    }
}