package com.ktun.ailabapp.presentation.ui.screens.announcement

import androidx.lifecycle.ViewModel
import com.ktun.ailabapp.data.model.Announcement
import com.ktun.ailabapp.data.model.AnnouncementFilter
import com.ktun.ailabapp.data.model.AnnouncementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AnnouncementUiState(
    val announcements: List<Announcement> = emptyList(),
    val selectedFilter: AnnouncementFilter = AnnouncementFilter.ALL,
    val isLoading: Boolean = false
)

class AnnouncementViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AnnouncementUiState())
    val uiState: StateFlow<AnnouncementUiState> = _uiState.asStateFlow()

    init {
        loadAnnouncements()
    }

    private fun loadAnnouncements() {
        // Mock data - Gerçek uygulamada API'den gelecek
        val mockAnnouncements = listOf(
            Announcement(
                id = "1",
                type = AnnouncementType.ALL,
                title = "Ai Lab Yönetim Kurulu",
                content = "Lorem ipsum dolor sit amet consectetur. Tortor aenean suspendisse pretium nunc non facilisi.",
                senderName = "Lab Yönetimi",
                senderImage = null,
                timestamp = "15.12.2025 09:00",
                isRead = false
            ),
            Announcement(
                id = "2",
                type = AnnouncementType.PERSONAL,
                title = "Şevval Karaman",
                content = "Lorem ipsum dolor sit amet consectetur. Tortor aenean suspendisse pretium nunc non facilisi. Lorem ipsum dolor sit amet consectetur. Tortor aenean suspendisse pretium nunc non facilisi.",
                senderName = "Şevval Karaman",
                senderImage = "https://i.pravatar.cc/150?img=1",
                timestamp = "14.12.2025 16:30",
                isRead = false
            ),
            Announcement(
                id = "3",
                type = AnnouncementType.ALL,
                title = "Ai Lab Yönetim Kurulu",
                content = "Lorem ipsum dolor sit amet consectetur. Tortor aenean suspendisse pretium nunc non facilisi.",
                senderName = "Lab Yönetimi",
                senderImage = null,
                timestamp = "14.12.2025 10:00",
                isRead = true
            ),
            Announcement(
                id = "4",
                type = AnnouncementType.PERSONAL,
                title = "Şevval Karaman",
                content = "Lorem ipsum dolor sit amet consectetur. Tortor aenean suspendisse pretium nunc non facilisi. Lorem ipsum dolor sit amet consectetur. Tortor aenean suspendisse pretium nunc non facilisi.",
                senderName = "Şevval Karaman",
                senderImage = "https://i.pravatar.cc/150?img=1",
                timestamp = "13.12.2025 14:20",
                isRead = true
            ),
            Announcement(
                id = "5",
                type = AnnouncementType.TEAM,
                title = "Takım Duyurusu",
                content = "Lorem ipsum dolor sit amet consectetur. Tortor aenean suspendisse pretium nunc non facilisi.",
                senderName = "Takım Lideri",
                senderImage = null,
                timestamp = "13.12.2025 11:00",
                isRead = false
            ),
            Announcement(
                id = "6",
                type = AnnouncementType.TEAM,
                title = "Sprint Toplantısı",
                content = "Bu hafta sprint toplantımız Perşembe günü saat 14:00'te olacaktır. Lütfen hazırlıklı gelin.",
                senderName = "Proje Yöneticisi",
                senderImage = null,
                timestamp = "12.12.2025 09:15",
                isRead = false
            ),
            Announcement(
                id = "7",
                type = AnnouncementType.ALL,
                title = "Laboratuvar Bakım Çalışması",
                content = "15-16 Aralık tarihlerinde laboratuvarımızda bakım çalışması yapılacaktır. Bu süre zarfında laboratuvar kapalı olacaktır.",
                senderName = "Lab Yönetimi",
                senderImage = null,
                timestamp = "11.12.2025 16:45",
                isRead = true
            ),
            Announcement(
                id = "8",
                type = AnnouncementType.PERSONAL,
                title = "Rapor Teslimi Hatırlatması",
                content = "Proje raporunuzun son teslim tarihi 20 Aralık'tır. Lütfen zamanında teslim etmeyi unutmayın.",
                senderName = "Akademik Danışman",
                senderImage = "https://i.pravatar.cc/150?img=3",
                timestamp = "10.12.2025 11:20",
                isRead = false
            )
        )

        _uiState.update { it.copy(announcements = mockAnnouncements) }
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
                it.type == AnnouncementType.ALL  // Lab genel duyuruları
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

    // Okunmamış duyuru sayısını getir (ileride badge için kullanılabilir)
    fun getUnreadCount(): Int {
        return _uiState.value.announcements.count { !it.isRead }
    }
}

