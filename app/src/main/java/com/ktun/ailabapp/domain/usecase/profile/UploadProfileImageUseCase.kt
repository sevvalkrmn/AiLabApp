package com.ktun.ailabapp.domain.usecase.profile

import android.net.Uri
import com.ktun.ailabapp.data.remote.dto.response.ProfileResponse
import com.ktun.ailabapp.domain.repository.IAuthRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class UploadProfileImageUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(userId: String, imageUri: Uri): NetworkResult<ProfileResponse> =
        repository.uploadAndUpdateProfileImage(userId, imageUri)
}
