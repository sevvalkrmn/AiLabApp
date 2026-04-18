package com.ktun.ailabapp.domain.usecase.profile

import com.ktun.ailabapp.domain.repository.IAuthRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(oldPassword: String, newPassword: String): NetworkResult<Unit> =
        repository.changePassword(oldPassword, newPassword)
}
