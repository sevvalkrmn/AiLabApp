package com.ktun.ailabapp.domain.repository

import com.ktun.ailabapp.data.remote.dto.response.AuthResponse
import com.ktun.ailabapp.util.NetworkResult

interface IAuthRepository {

    suspend fun login(
        email: String,
        password: String,
        rememberMe: Boolean
    ): NetworkResult<AuthResponse>

    suspend fun completeRegistration(
        idToken: String,
        fullName: String,
        surname: String,
        username: String,
        email: String,
        schoolNumber: String,
        phone: String
    ): NetworkResult<AuthResponse>
}
