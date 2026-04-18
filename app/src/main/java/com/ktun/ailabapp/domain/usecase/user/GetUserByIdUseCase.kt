package com.ktun.ailabapp.domain.usecase.user

import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.domain.repository.IUserRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val repository: IUserRepository
) {
    suspend operator fun invoke(userId: String): NetworkResult<User> =
        repository.getUserById(userId)
}
