package com.ktun.ailabapp.presentation.ui.screens.announcement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.model.Announcement
import com.ktun.ailabapp.data.model.AnnouncementFilter
import com.ktun.ailabapp.data.model.AnnouncementType
import com.ktun.ailabapp.data.repository.AnnouncementRepository
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
    private val repository: AnnouncementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnnouncementUiState())
    val uiState: StateFlow<AnnouncementUiState> = _uiState.asStateFlow()

    init {
        loadAnnouncements()
    }

    fun loadAnnouncements() {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getMyAnnouncements().fold(
                onSuccess = { announcements ->
                    _uiState.update {
                        it.copy(
                            announcements = announcements,
                            isLoading = false
                        )
                    }
                    // Detayları arka planda paralel yükle, tek state güncellemesi
                    loadAllDetails(announcements)
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Duyurular yüklenemedi"
                        )
                    }
                }
            )
        }
    }

    private fun loadAllDetails(announcements: List<Announcement>) {
        viewModelScope.launch {
            val detailedList = announcements.map { announcement ->
                async {
                    repository.getAnnouncementDetail(announcement.id).fold(
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
            // ✅ Önce eski isRead değerini sakla
            val oldAnnouncement = _uiState.value.announcements.find { it.id == id }
            val oldIsRead = oldAnnouncement?.isRead

            repository.getAnnouncementDetail(id).fold(
                onSuccess = { detailAnnouncement ->
                    _uiState.update { currentState ->
                        val updatedAnnouncements = currentState.announcements.map { announcement ->
                            if (announcement.id == id) {
                                // ✅ Eski isRead değerini parametre olarak gönder
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
            AnnouncementFilter.GENERAL -> announcements.filter {
                it.type == AnnouncementType.ALL
            }
            AnnouncementFilter.TEAM -> announcements.filter {
                it.type == AnnouncementType.TEAM
            }
            AnnouncementFilter.PERSONAL -> announcements.filter {
                it.type == AnnouncementType.PERSONAL
            }
        }
    }

    fun markAsRead(announcementId: String) {
        viewModelScope.launch {
            // Backend'e okundu işareti gönder
            repository.markAsRead(announcementId)

            // UI'da güncelle
            _uiState.update { currentState ->
                val updatedAnnouncements = currentState.announcements.map { announcement ->
                    if (announcement.id == announcementId) {
                        announcement.copy(isRead = true)
                    } else {
                        announcement
                    }
                }
                currentState.copy(announcements = updatedAnnouncements)
            }
        }
    }

    fun getUnreadCount(): Int {
        return _uiState.value.announcements.count { !it.isRead }
    }

    //Logout olurken duyuruları temizle
    fun clearAnnouncements() {
        Logger.d("Clearing all announcements and state", tag = "AnnouncementVM")
        _uiState.value = AnnouncementUiState() // Tüm state'i sıfırla
    }
}