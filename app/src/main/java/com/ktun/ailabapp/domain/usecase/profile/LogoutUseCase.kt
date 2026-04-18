package com.ktun.ailabapp.domain.usecase.profile

import com.ktun.ailabapp.domain.repository.IAuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke() = repository.logout()
}
