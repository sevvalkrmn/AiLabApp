package com.ktun.ailabapp.presentation.ui.screens.announcement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.model.Announcement
import com.ktun.ailabapp.data.model.AnnouncementFilter
import com.ktun.ailabapp.data.model.AnnouncementType
import com.ktun.ailabapp.domain.usecase.announcement.GetAnnouncementDetailUseCase
import com.ktun.ailabapp.domain.usecase.announcement.GetMyAnnouncementsUseCase
import com.ktun.ailabapp.domain.usecase.announcement.MarkAnnouncementAsReadUseCase
import com.ktun.ailabapp.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnnouncementUiState(
    val announcements: List<Announcement> = emptyList(),
    val selectedFilter: AnnouncementFilter = AnnouncementFilter.ALL,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AnnouncementViewModel @Inject constructor(
    private val getMyAnnouncementsUseCase: GetMyAnnouncementsUseCase,
    private val getAnnouncementDetailUseCase: GetAnnouncementDetailUseCase,
    private val markAnnouncementAsReadUseCase: MarkAnnouncementAsReadUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnnouncementUiState())
    val uiState: StateFlow<AnnouncementUiState> = _uiState.asStateFlow()

    init {
        loadAnnouncements()
    }

    fun loadAnnouncements(forceReload: Boolean = false) {
        if (_uiState.value.isLoading) return
        if (!forceReload && _uiState.value.announcements.isNotEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getMyAnnouncementsUseCase().fold(
                onSuccess = { announcements ->
                    _uiState.update { it.copy(announcements = announcements, isLoading = false) }
                    loadAllDetails(announcements)
                },
                onFailure = { exception ->
                    _uiState.update { it.copy(isLoading = false, error = exception.message ?: "Duyurular yüklenemedi") }
                }
            )
        }
    }

    private fun loadAllDetails(announcements: List<Announcement>) {
        viewModelScope.launch {
            val detailedList = announcements.map { announcement ->
                async {
                    getAnnouncementDetailUseCase(announcement.id).fold(
                        onSuccess = { detail -> detail.copy(isRead = announcement.isRead) },
                        onFailure = { announcement }
                    )
                }
            }.awaitAll()

            _uiState.update { it.copy(announcements = detailedList) }
        }
    }

    fun loadAnnouncementDetail(id: String) {
        viewModelScope.launch {
            val oldAnnouncement = _uiState.value.announcements.find { it.id == id }
            val oldIsRead = oldAnnouncement?.isRead

            getAnnouncementDetailUseCase(id).fold(
                onSuccess = { detailAnnouncement ->
                    _uiState.update { currentState ->
                        val updatedAnnouncements = currentState.announcements.map { announcement ->
                            if (announcement.id == id) {
                                detailAnnouncement.copy(isRead = oldIsRead ?: detailAnnouncement.isRead)
                            } else {
                                announcement
                            }
                        }
                        currentState.copy(announcements = updatedAnnouncements)
                    }
                },
                onFailure = {
                    Logger.e("Detay yüklenemedi: $id", tag = "AnnouncementVM")
                }
            )
        }
    }

    fun setFilter(filter: AnnouncementFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun getFilteredAnnouncements(): List<Announcement> {
        val filter = _uiState.value.selectedFilter
        val announcements = _uiState.value.announcements

        return when (filter) {
            AnnouncementFilter.ALL -> announcements
            AnnouncementFilter.GENERAL -> announcements.filter { it.type == AnnouncementType.ALL }
            AnnouncementFilter.TEAM -> announcements.filter { it.type == AnnouncementType.TEAM }
            AnnouncementFilter.PERSONAL -> announcements.filter { it.type == AnnouncementType.PERSONAL }
        }
    }

    fun markAsRead(announcementId: String) {
        viewModelScope.launch {
            markAnnouncementAsReadUseCase(announcementId)

            _uiState.update { currentState ->
                val updatedAnnouncements = currentState.announcements.map { announcement ->
                    if (announcement.id == announcementId) announcement.copy(isRead = true)
                    else announcement
                }
                currentState.copy(announcements = updatedAnnouncements)
            }
        }
    }

    fun getUnreadCount(): Int = _uiState.value.announcements.count { !it.isRead }

    fun clearAnnouncements() {
        Logger.d("Clearing all announcements and state", tag = "AnnouncementVM")
        _uiState.value = AnnouncementUiState()
        hasAnimated = false
    }

    var hasAnimated = false
        private set

    fun markAnimated() { hasAnimated = true }
}
