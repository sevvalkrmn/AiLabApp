package com.ktun.ailabapp.domain.usecase.profile

import com.ktun.ailabapp.domain.repository.IAuthRepository
import javax.inject.Inject

class InvalidateProfileCacheUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    operator fun invoke() = repository.invalidateProfileCache()
}
