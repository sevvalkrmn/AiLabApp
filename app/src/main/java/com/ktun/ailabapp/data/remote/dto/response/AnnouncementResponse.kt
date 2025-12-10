package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName
import com.ktun.ailabapp.data.model.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// ==================== API RESPONSE MODELS ====================

// GET /api/Announcements/my response
data class AnnouncementsResponse(
    @SerializedName("items") val items: List<AnnouncementItem>,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("hasPrevious") val hasPrevious: Boolean,
    @SerializedName("hasNext") val hasNext: Boolean
)

data class AnnouncementItem(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("scope") val scope: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("isRead") val isRead: Boolean,
    @SerializedName("preview") val preview: String?
)

// GET /api/Announcements/{id} response
data class AnnouncementDetailResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("scope") val scope: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("createdBy") val createdBy: String,
    @SerializedName("createdByName") val createdByName: String?,
    @SerializedName("createdByEmail") val createdByEmail: String?,
    @SerializedName("announcementProjects") val announcementProjects: List<AnnouncementProject>?,
    @SerializedName("announcementUsers") val announcementUsers: List<AnnouncementUser>?,
    @SerializedName("isRead") val isRead: Boolean? = null // ✅ EKLE - Backend'den gelmeyebilir
)

data class AnnouncementProject(
    @SerializedName("projectId") val projectId: String,
    @SerializedName("projectName") val projectName: String?,
    @SerializedName("userCount") val userCount: Int,
    @SerializedName("readCount") val readCount: Int
)

data class AnnouncementUser(
    @SerializedName("userId") val userId: String,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("isRead") val isRead: Boolean,
    @SerializedName("readAt") val readAt: String?
)

// ==================== MAPPER FUNCTIONS ====================

// API response'u UI modeline çevir
fun AnnouncementItem.toAnnouncement(): Announcement {
    return Announcement(
        id = this.id,
        type = when (this.scope) {
            0 -> AnnouncementType.ALL
            1 -> AnnouncementType.TEAM
            2 -> AnnouncementType.PERSONAL
            else -> AnnouncementType.ALL
        },
        title = this.title,
        content = this.preview ?: "",
        senderName = "AI Lab",
        senderImage = null,
        timestamp = formatDate(this.createdAt),
        isRead = this.isRead
    )
}

// ✅ PARAMETRE EKLE: Eski isRead değerini gönderebilmek için
fun AnnouncementDetailResponse.toAnnouncement(keepIsRead: Boolean? = null): Announcement {
    return Announcement(
        id = this.id,
        type = when (this.scope) {
            0 -> AnnouncementType.ALL
            1 -> AnnouncementType.TEAM
            2 -> AnnouncementType.PERSONAL
            else -> AnnouncementType.ALL
        },
        title = this.title,
        content = this.content,
        senderName = this.createdByName ?: "AI Lab",
        senderImage = null,
        timestamp = formatDate(this.createdAt),
        isRead = keepIsRead ?: this.isRead ?: false // ✅ 1. Parametre, 2. Backend, 3. Default false
    )
}

private fun formatDate(isoDate: String): String {
    return try {
        val zonedDateTime = ZonedDateTime.parse(isoDate)
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale("tr"))
        zonedDateTime.format(formatter)
    } catch (e: Exception) {
        isoDate
    }
}