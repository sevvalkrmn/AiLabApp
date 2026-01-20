package com.ktun.ailabapp.data.repository

import com.ktun.ailabapp.data.model.Announcement
import com.ktun.ailabapp.data.remote.api.AnnouncementApi
import com.ktun.ailabapp.data.remote.dto.response.toAnnouncement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AnnouncementRepository @Inject constructor(
    private val announcementApi: AnnouncementApi
) {

    suspend fun getMyAnnouncements(
        pageNumber: Int = 1,
        pageSize: Int = 20,
        isRead: Boolean? = null
    ): Result<List<Announcement>> = withContext(Dispatchers.IO) {
        try {
            val response = announcementApi.getMyAnnouncements(pageNumber, pageSize, isRead)

            if (response.isSuccessful) {
                val announcements = response.body()?.items?.map { it.toAnnouncement() } ?: emptyList()
                Result.success(announcements)
            } else {
                Result.failure(Exception("Duyurular yüklenemedi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAnnouncementDetail(id: String): Result<Announcement> = withContext(Dispatchers.IO) {
        try {
            val response = announcementApi.getAnnouncementDetail(id)

            if (response.isSuccessful) {
                val announcement = response.body()?.toAnnouncement()
                if (announcement != null) {
                    Result.success(announcement)
                } else {
                    Result.failure(Exception("Duyuru bulunamadı"))
                }
            } else {
                Result.failure(Exception("Duyuru detayı yüklenemedi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAsRead(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = announcementApi.markAsRead(id)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Okundu işareti eklenemedi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAnnouncement(
        title: String,
        content: String,
        scope: Int,
        userId: String? = null,
        projectId: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val targetUserIds = if (userId != null) listOf(userId) else null
            val targetProjectIds = if (projectId != null) listOf(projectId) else null

            val request = com.ktun.ailabapp.data.remote.dto.request.CreateAnnouncementRequest(
                title = title,
                content = content,
                scope = scope,
                targetUserIds = targetUserIds,
                targetProjectIds = targetProjectIds
            )

            val response = announcementApi.createAnnouncement(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Duyuru oluşturulamadı: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}