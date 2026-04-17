package com.ktun.ailabapp.domain.usecase.auth

import javax.inject.Inject

class ValidateRegistrationStep1UseCase @Inject constructor() {

    operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): String? = when {
        email.isBlank() -> "E-posta boş bırakılamaz"
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Geçerli bir e-posta adresi girin"
        password.isBlank() -> "Şifre boş bırakılamaz"
        password.length < 8 -> "Şifre en az 8 karakter olmalıdır"
        !password.any { it.isUpperCase() } -> "Şifre en az 1 büyük harf içermelidir"
        confirmPassword.isBlank() -> "Şifre tekrar boş bırakılamaz"
        password != confirmPassword -> "Şifreler eşleşmiyor"
        else -> null
    }
}
