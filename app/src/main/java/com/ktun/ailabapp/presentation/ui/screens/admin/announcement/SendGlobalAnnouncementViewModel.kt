package com.ktun.ailabapp.presentation.ui.screens.admin.announcement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.repository.AnnouncementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SendGlobalAnnouncementUiState(
    val title: String = "",
    val content: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SendGlobalAnnouncementViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SendGlobalAnnouncementUiState())
    val uiState: StateFlow<SendGlobalAnnouncementUiState> = _uiState.asStateFlow()

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, errorMessage = null) }
    }

    fun onContentChange(newContent: String) {
        _uiState.update { it.copy(content = newContent, errorMessage = null) }
    }

    fun sendAnnouncement() {
        val title = _uiState.value.title.trim()
        val content = _uiState.value.content.trim()

        if (title.isBlank() || content.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Başlık ve içerik boş olamaz") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 0: ALL (Genel Duyuru)
            val result = announcementRepository.createAnnouncement(
                title = title,
                content = content,
                scope = 0, 
                userId = null,
                projectId = null
            )

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = exception.message ?: "Duyuru gönderilemedi"
                        ) 
                    }
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = SendGlobalAnnouncementUiState()
    }
}
