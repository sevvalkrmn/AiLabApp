package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

// POST /api/Announcements request
data class CreateAnnouncementRequest(
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("scope") val scope: Int,
    @SerializedName("targetProjectIds") val targetProjectIds: List<String>? = null,
    @SerializedName("targetUserIds") val targetUserIds: List<String>? = null
)