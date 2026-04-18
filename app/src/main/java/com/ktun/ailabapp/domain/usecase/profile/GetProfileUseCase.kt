package com.ktun.ailabapp.domain.usecase.profile

import com.ktun.ailabapp.data.remote.dto.response.ProfileResponse
import com.ktun.ailabapp.domain.repository.IAuthRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(): NetworkResult<ProfileResponse> = repository.getProfile()
}
