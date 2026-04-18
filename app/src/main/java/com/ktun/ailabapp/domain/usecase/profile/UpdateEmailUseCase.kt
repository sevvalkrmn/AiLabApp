package com.ktun.ailabapp.domain.usecase.profile

import com.ktun.ailabapp.domain.repository.IAuthRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class UpdateEmailUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(password: String, newEmail: String): NetworkResult<Unit> =
        repository.updateEmail(password, newEmail)
}
