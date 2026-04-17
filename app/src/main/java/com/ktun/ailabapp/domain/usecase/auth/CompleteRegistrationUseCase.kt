package com.ktun.ailabapp.domain.usecase.auth

import com.ktun.ailabapp.data.remote.dto.response.AuthResponse
import com.ktun.ailabapp.domain.repository.IAuthRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class CompleteRegistrationUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(
        idToken: String,
        fullName: String,
        surname: String,
        username: String,
        email: String,
        schoolNumber: String,
        phone: String
    ): NetworkResult<AuthResponse> {
        return authRepository.completeRegistration(
            idToken = idToken,
            fullName = fullName,
            surname = surname,
            username = username,
            email = email,
            schoolNumber = schoolNumber,
            phone = phone
        )
    }
}
