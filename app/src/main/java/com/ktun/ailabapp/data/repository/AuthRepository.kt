package com.ktun.ailabapp.data.repository

import com.ktun.ailabapp.data.model.User
import kotlinx.coroutines.delay

class AuthRepository {

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            // Simüle edilmiş login
            delay(1000)

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val user = User(1, "AI Lab Kullanıcısı", email)
                Result.success(user)
            } else {
                Result.failure(Exception("Email veya şifre boş olamaz"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

