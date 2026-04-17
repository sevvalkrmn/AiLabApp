package com.ktun.ailabapp.domain.usecase.auth

import com.ktun.ailabapp.util.FirebaseAuthManager
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val authManager: FirebaseAuthManager
) {
    suspend operator fun invoke(email: String): Result<String> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Lütfen e-posta adresinizi girin"))
        }
        val result = authManager.sendPasswordResetEmail(email)
        return if (result.isSuccess) {
            Result.success("Şifre sıfırlama bağlantısı e-posta adresinize gönderildi")
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Bir hata oluştu"))
        }
    }
}
