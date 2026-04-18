package com.ktun.ailabapp.domain.repository

import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.util.NetworkResult

interface IUserRepository {
    suspend fun getAllUsers(pageNumber: Int, pageSize: Int): NetworkResult<List<User>>
    suspend fun getUserById(userId: String): NetworkResult<User>
    suspend fun updateUserProfileImage(userId: String, imageUrl: String): NetworkResult<String>
    suspend fun deleteUser(userId: String): NetworkResult<Unit>
}
