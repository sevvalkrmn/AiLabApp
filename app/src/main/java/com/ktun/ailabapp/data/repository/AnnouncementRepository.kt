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
}