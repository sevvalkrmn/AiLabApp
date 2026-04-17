package com.ktun.ailabapp.domain.usecase.auth

import com.ktun.ailabapp.data.remote.dto.response.AuthResponse
import com.ktun.ailabapp.domain.repository.IAuthRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        rememberMe: Boolean
    ): NetworkResult<AuthResponse> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) return NetworkResult.Error("E-posta boş bırakılamaz")
        if (password.isBlank()) return NetworkResult.Error("Şifre boş bırakılamaz")
        return authRepository.login(trimmedEmail, password, rememberMe)
    }
}
