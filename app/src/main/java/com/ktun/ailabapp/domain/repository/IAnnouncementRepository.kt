package com.ktun.ailabapp.domain.repository

import com.ktun.ailabapp.data.model.Announcement

interface IAnnouncementRepository {
    suspend fun getMyAnnouncements(pageNumber: Int, pageSize: Int, isRead: Boolean?): Result<List<Announcement>>
    suspend fun getAnnouncementDetail(id: String): Result<Announcement>
    suspend fun markAsRead(id: String): Result<Unit>
    suspend fun createAnnouncement(title: String, content: String, scope: Int, userId: String?, projectId: String?): Result<Unit>
}
