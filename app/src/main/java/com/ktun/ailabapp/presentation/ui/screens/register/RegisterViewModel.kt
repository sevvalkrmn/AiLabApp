package com.ktun.ailabapp.presentation.ui.screens.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateFirstName(value: String) {
        _uiState.update { it.copy(firstName = value) }
    }

    fun updateLastName(value: String) {
        _uiState.update { it.copy(lastName = value) }
    }

    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun updatePhone(value: String) {
        // Sadece rakam kabul et ve 10 haneden fazla olmasın
        val filtered = value.filter { it.isDigit() }.take(10)
        _uiState.update { it.copy(phone = filtered) }
    }

    fun updatePassword(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun updateConfirmPassword(value: String) {
        _uiState.update { it.copy(confirmPassword = value) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value

        // Validasyon
        when {
            state.firstName.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Lütfen adınızı girin") }
                return
            }
            state.lastName.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Lütfen soyadınızı girin") }
                return
            }
            state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches() -> {
                _uiState.update { it.copy(errorMessage = "Geçerli bir email adresi girin") }
                return
            }
            state.phone.length != 10 -> {
                _uiState.update { it.copy(errorMessage = "Telefon numarası 10 haneli olmalıdır") }
                return
            }
            state.password.length < 6 -> {
                _uiState.update { it.copy(errorMessage = "Şifre en az 6 karakter olmalıdır") }
                return
            }
            state.password != state.confirmPassword -> {
                _uiState.update { it.copy(errorMessage = "Şifreler eşleşmiyor") }
                return
            }
        }

        // Loading başlat
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        // TODO: API çağrısı yapılacak
        // Şimdilik mock başarılı kayıt
        kotlinx.coroutines.GlobalScope.launch {
            kotlinx.coroutines.delay(1500) // Simüle edilmiş API çağrısı
            _uiState.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }
}