package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class PendingTaskResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("assigneeId")
    val assigneeId: String?,

    @SerializedName("assigneeName")
    val assigneeName: String?,

    @SerializedName("projectId")
    val projectId: String?,

    @SerializedName("projectName")
    val projectName: String?,

    @SerializedName("dueDate")
    val dueDate: String?
)
