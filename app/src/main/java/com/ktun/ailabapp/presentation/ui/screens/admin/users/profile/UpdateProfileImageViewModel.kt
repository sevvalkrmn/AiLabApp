package com.ktun.ailabapp.presentation.ui.screens.admin.users.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.remote.api.UsersApi
import com.ktun.ailabapp.data.remote.dto.request.UpdateProfileImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UpdateProfileImageUiState(
    val imageUrl: String = "",
    val availableAvatars: List<String> = AvatarConstants.DEFAULT_AVATARS,
    val isLoadingAvatars: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val inputError: String? = null
)

@HiltViewModel
class UpdateProfileImageViewModel @Inject constructor(
    private val userApi: UsersApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateProfileImageUiState())
    val uiState: StateFlow<UpdateProfileImageUiState> = _uiState.asStateFlow()

    fun onImageUrlChange(url: String) {
        _uiState.update {
            it.copy(
                imageUrl = url,
                inputError = null
            )
        }
    }

    fun updateProfileImage(userId: String) {
        val imageUrl = _uiState.value.imageUrl.trim()

        // Validation
        if (imageUrl.isBlank()) {
            _uiState.update { it.copy(inputError = "URL boş olamaz") }
            return
        }

        // ✅ Default avatarlardan biri mi kontrol et
        if (imageUrl !in _uiState.value.availableAvatars) {
            _uiState.update { it.copy(inputError = "Sadece sistem avatarlarından seçim yapabilirsiniz") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val response = userApi.updateUserProfileImage(
                    userId = userId,
                    request = UpdateProfileImageRequest(profileImageUrl = imageUrl)
                )

                if (response.isSuccessful) {
                    android.util.Log.d("UpdateProfileImageVM", "✅ Image updated: $imageUrl")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("UpdateProfileImageVM", "❌ Error ${response.code()}: $errorBody")

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Fotoğraf güncellenemedi: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("UpdateProfileImageVM", "❌ Exception: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Hata: ${e.message}"
                    )
                }
            }
        }
    }
}

// ✅ Static avatar listesi (fallback)
object AvatarConstants {
    val DEFAULT_AVATARS = listOf(
        "https://api.dicebear.com/7.x/avataaars/svg?seed=Felix",
        "https://api.dicebear.com/7.x/avataaars/svg?seed=Aneka",
        "https://api.dicebear.com/7.x/avataaars/svg?seed=Bailey",
        "https://api.dicebear.com/7.x/avataaars/svg?seed=Charlie",
        "https://api.dicebear.com/7.x/avataaars/svg?seed=Max",
        "https://api.dicebear.com/7.x/avataaars/svg?seed=Luna",
        "https://api.dicebear.com/7.x/avataaars/svg?seed=Oliver",
        "https://api.dicebear.com/7.x/avataaars/svg?seed=Zoe"
    )
}