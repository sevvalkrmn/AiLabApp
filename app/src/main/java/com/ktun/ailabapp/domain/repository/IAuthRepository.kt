package com.ktun.ailabapp.domain.repository

import android.net.Uri
import com.ktun.ailabapp.data.remote.dto.response.AuthResponse
import com.ktun.ailabapp.data.remote.dto.response.LeaderboardUserResponse
import com.ktun.ailabapp.data.remote.dto.response.ProfileResponse
import com.ktun.ailabapp.util.NetworkResult

interface IAuthRepository {

    suspend fun login(
        email: String,
        password: String,
        rememberMe: Boolean
    ): NetworkResult<AuthResponse>

    suspend fun completeRegistration(
        idToken: String,
        fullName: String,
        surname: String,
        username: String,
        email: String,
        schoolNumber: String,
        phone: String
    ): NetworkResult<AuthResponse>

    suspend fun getProfile(): NetworkResult<ProfileResponse>

    suspend fun updateEmail(password: String, newEmail: String): NetworkResult<Unit>

    suspend fun changePassword(oldPassword: String, newPassword: String): NetworkResult<Unit>

    suspend fun updatePhone(newPhone: String): NetworkResult<Unit>

    suspend fun logout()

    suspend fun getDefaultAvatars(): NetworkResult<List<String>>

    suspend fun getLeaderboard(): NetworkResult<List<LeaderboardUserResponse>>

    suspend fun uploadAndUpdateProfileImage(userId: String, imageUri: Uri): NetworkResult<ProfileResponse>

    suspend fun selectDefaultAvatar(avatarUrl: String): NetworkResult<ProfileResponse>

    fun invalidateProfileCache()
}
