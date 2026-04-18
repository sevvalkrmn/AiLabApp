package com.ktun.ailabapp.domain.usecase.user

import com.ktun.ailabapp.domain.repository.IUserRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class UpdateUserProfileImageUseCase @Inject constructor(
    private val repository: IUserRepository
) {
    suspend operator fun invoke(userId: String, imageUrl: String): NetworkResult<String> =
        repository.updateUserProfileImage(userId, imageUrl)
}
