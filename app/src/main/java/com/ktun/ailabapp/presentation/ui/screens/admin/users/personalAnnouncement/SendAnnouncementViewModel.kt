package com.ktun.ailabapp.presentation.ui.screens.admin.users.personalAnnouncement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.remote.api.AnnouncementApi
import com.ktun.ailabapp.data.remote.dto.request.CreateAnnouncementRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SendAnnouncementUiState(
    val title: String = "",
    val content: String = "",
    val titleError: String? = null,
    val contentError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SendAnnouncementViewModel @Inject constructor(
    private val announcementApi: AnnouncementApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(SendAnnouncementUiState())
    val uiState: StateFlow<SendAnnouncementUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) {
        _uiState.update {
            it.copy(
                title = title,
                titleError = null
            )
        }
    }

    fun onContentChange(content: String) {
        _uiState.update {
            it.copy(
                content = content,
                contentError = null
            )
        }
    }

    fun sendAnnouncement(userId: String) {
        // Validation
        val titleError = when {
            _uiState.value.title.isBlank() -> "Başlık boş olamaz"
            _uiState.value.title.length < 3 -> "Başlık en az 3 karakter olmalı"
            else -> null
        }

        val contentError = when {
            _uiState.value.content.isBlank() -> "İçerik boş olamaz"
            _uiState.value.content.length < 10 -> "İçerik en az 10 karakter olmalı"
            else -> null
        }

        if (titleError != null || contentError != null) {
            _uiState.update {
                it.copy(
                    titleError = titleError,
                    contentError = contentError
                )
            }
            return
        }

        // API Call
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val request = CreateAnnouncementRequest(
                    title = _uiState.value.title,
                    content = _uiState.value.content,
                    scope = 2, // PERSONAL
                    targetUserIds = listOf(userId)
                )

                val response = announcementApi.createAnnouncement(request)

                if (response.isSuccessful) {
                    android.util.Log.d("SendAnnouncementVM", "✅ Announcement sent successfully")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                } else {
                    android.util.Log.e("SendAnnouncementVM", "❌ Error: ${response.code()}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Duyuru gönderilemedi: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("SendAnnouncementVM", "❌ Exception: ${e.message}")
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