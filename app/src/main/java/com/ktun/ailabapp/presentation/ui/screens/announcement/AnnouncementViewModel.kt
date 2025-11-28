package com.ktunailab.ailabapp.presentation.ui.screens.announcement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktunailab.ailabapp.data.model.Announcement
import com.ktunailab.ailabapp.data.model.AnnouncementFilter
import com.ktunailab.ailabapp.data.model.AnnouncementType
import com.ktunailab.ailabapp.data.repository.AnnouncementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

                    // ✅ EKLE: Her duyurunun detayını arka planda yükle
                    announcements.forEach { announcement ->
                        loadAnnouncementDetail(announcement.id)
                    }
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
                    println("⚠️ Detay yüklenemedi: $id")
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
        val count = _uiState.value.announcements.count { !it.isRead }
        println("🔔 Okunmamış duyuru sayısı: $count")
        println("📋 Tüm duyurular: ${_uiState.value.announcements.map { "id=${it.id}, isRead=${it.isRead}" }}")
        return count
    }

    //Logout olurken duyuruları temizle
    fun clearAnnouncements() {
        android.util.Log.d("AnnouncementViewModel", "🗑️ Clearing all announcements and state")
        _uiState.value = AnnouncementUiState() // Tüm state'i sıfırla
    }
}