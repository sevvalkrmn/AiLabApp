package com.ktun.ailabapp.presentation.ui.screens.admin.users.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.domain.usecase.profile.GetDefaultAvatarsUseCase
import com.ktun.ailabapp.domain.usecase.user.UpdateUserProfileImageUseCase
import com.ktun.ailabapp.util.FirebaseStorageHelper
import com.ktun.ailabapp.util.ImageCompressor
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UpdateProfileImageUiState(
    val imageUrl: String = "",
    val availableAvatars: List<String> = emptyList(),
    val isLoadingAvatars: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val inputError: String? = null
)

@HiltViewModel
class UpdateProfileImageViewModel @Inject constructor(
    private val updateUserProfileImageUseCase: UpdateUserProfileImageUseCase,
    private val getDefaultAvatarsUseCase: GetDefaultAvatarsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateProfileImageUiState())
    val uiState: StateFlow<UpdateProfileImageUiState> = _uiState.asStateFlow()

    init {
        loadDefaultAvatars()
    }

    private fun loadDefaultAvatars() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAvatars = true) }

            when (val result = getDefaultAvatarsUseCase()) {
                is NetworkResult.Success -> {
                    result.data?.let { avatars ->
                        _uiState.update { it.copy(availableAvatars = avatars, isLoadingAvatars = false) }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoadingAvatars = false, errorMessage = "Varsayılan avatarlar yüklenemedi: ${result.message}") }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun onImageUrlChange(url: String) {
        _uiState.update { it.copy(imageUrl = url, inputError = null) }
    }

    fun uploadAndUpdateImage(context: Context, userId: String, imageUri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val compressResult = ImageCompressor.compressToWebP(context, imageUri)
                if (compressResult.isFailure) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Resim işlenemedi") }
                    return@launch
                }

                val optimizedUri = compressResult.getOrNull()!!
                val uploadResult = FirebaseStorageHelper.uploadProfileImage(userId, optimizedUri)
                if (uploadResult.isFailure) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Firebase yükleme hatası: ${uploadResult.exceptionOrNull()?.message}") }
                    return@launch
                }

                val downloadUrl = uploadResult.getOrNull()!!
                updateProfileImage(userId, downloadUrl)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updateProfileImage(userId: String, url: String? = null) {
        val finalUrl = url ?: _uiState.value.imageUrl.trim()

        if (finalUrl.isBlank()) {
            _uiState.update { it.copy(inputError = "URL boş olamaz") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = updateUserProfileImageUseCase(userId, finalUrl)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
