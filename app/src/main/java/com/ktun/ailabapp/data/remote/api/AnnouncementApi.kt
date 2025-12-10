package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.request.CreateAnnouncementRequest
import com.ktun.ailabapp.data.remote.dto.response.AnnouncementDetailResponse
import com.ktun.ailabapp.data.remote.dto.response.AnnouncementsResponse
import retrofit2.Response
import retrofit2.http.*

interface AnnouncementApi {

    @GET("api/Announcements/my")
    suspend fun getMyAnnouncements(
        @Query("PageNumber") pageNumber: Int = 1,
        @Query("PageSize") pageSize: Int = 20,
        @Query("isRead") isRead: Boolean? = null
    ): Response<AnnouncementsResponse>

    @GET("api/Announcements/{id}")
    suspend fun getAnnouncementDetail(
        @Path("id") id: String
    ): Response<AnnouncementDetailResponse>

    @PUT("api/Announcements/{id}/read")
    suspend fun markAsRead(
        @Path("id") id: String
    ): Response<Unit>

    @POST("api/Announcements")
    suspend fun createAnnouncement(
        @Body request: CreateAnnouncementRequest
    ): Response<String>
}