package com.ktun.ailabapp.domain.usecase.auth

import javax.inject.Inject

class ValidateRegistrationStep2UseCase @Inject constructor() {

    operator fun invoke(
        fullName: String,
        surname: String,
        username: String,
        schoolNumber: String,
        phone: String
    ): String? = when {
        fullName.isBlank() -> "Ad boş bırakılamaz"
        surname.isBlank() -> "Soyad boş bırakılamaz"
        username.isBlank() -> "Kullanıcı adı boş bırakılamaz"
        username.length < 3 -> "Kullanıcı adı en az 3 karakter olmalıdır"
        schoolNumber.isBlank() -> "Okul numarası boş bırakılamaz"
        phone.isBlank() -> "Telefon numarası boş bırakılamaz"
        !phone.matches("^5[0-9]{9}$".toRegex()) -> "Telefon numarası 5 ile başlamalı ve 10 haneli olmalıdır"
        else -> null
    }
}
