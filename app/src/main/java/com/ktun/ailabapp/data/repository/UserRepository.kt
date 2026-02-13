package com.ktun.ailabapp.data.repository

import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.data.remote.api.UsersApi
import com.ktun.ailabapp.data.remote.dto.response.toUser
import com.ktun.ailabapp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val usersApi: UsersApi,
    private val projectRepository: ProjectRepository
) {
    suspend fun getAllUsers(
        pageNumber: Int = 1,
        pageSize: Int = 50
    ): NetworkResult<List<User>> = withContext(Dispatchers.IO) {
        try {
            val response = usersApi.getAllUsers(pageNumber, pageSize)

            when {
                response.code() == 401 -> {
                    NetworkResult.Error("Oturum süresi doldu")
                }
                response.isSuccessful && response.body() != null -> {
                    val users = response.body()!!.items.map { it.toUser() }
                    NetworkResult.Success(users)
                }
                else -> {
                    NetworkResult.Error("Kullanıcılar yüklenemedi: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    suspend fun getUserById(userId: String): NetworkResult<User> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("UserRepository", "🔍 Fetching user: $userId")

            val response = usersApi.getUserById(userId)

            when {
                response.code() == 401 -> {
                    NetworkResult.Error("Oturum süresi doldu")
                }
                response.code() == 403 -> {
                    NetworkResult.Error("Bu kullanıcıyı görme yetkiniz yok")
                }
                response.code() == 404 -> {
                    NetworkResult.Error("Kullanıcı bulunamadı")
                }
                response.isSuccessful && response.body() != null -> {
                    var user = response.body()!!.toUser()

                    when (val projectsResult = projectRepository.getUserProjects(userId)) {
                        is NetworkResult.Success -> {
                            user = user.copy(projects = projectsResult.data)
                        }
                        else -> {}
                    }

                    NetworkResult.Success(user)
                }
                else -> {
                    NetworkResult.Error("Kullanıcı yüklenemedi: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "❌ Error: ${e.message}", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    suspend fun updateUserProfileImage(
        userId: String,
        imageUrl: String
    ): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val request = com.ktun.ailabapp.data.remote.dto.request.UpdateProfileImageRequest(imageUrl)
            val response = usersApi.updateUserProfileImage(userId, request)

            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!.url)
            } else {
                NetworkResult.Error("Fotoğraf güncellenemedi: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    suspend fun deleteUser(userId: String): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = usersApi.deleteUser(userId)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Kullanıcı silinemedi: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }
}
