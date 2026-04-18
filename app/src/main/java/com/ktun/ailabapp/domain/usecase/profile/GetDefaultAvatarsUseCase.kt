package com.ktun.ailabapp.domain.usecase.profile

import com.ktun.ailabapp.domain.repository.IAuthRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class GetDefaultAvatarsUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(): NetworkResult<List<String>> = repository.getDefaultAvatars()
}
