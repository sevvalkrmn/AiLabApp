package com.ktun.ailabapp.domain.usecase.user

import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.domain.repository.IUserRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(
    private val repository: IUserRepository
) {
    suspend operator fun invoke(pageNumber: Int = 1, pageSize: Int = 50): NetworkResult<List<User>> =
        repository.getAllUsers(pageNumber, pageSize)
}
