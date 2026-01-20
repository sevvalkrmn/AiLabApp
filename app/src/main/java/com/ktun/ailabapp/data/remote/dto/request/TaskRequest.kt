package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class CreateTaskRequest(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("projectId")
    val projectId: String,

    @SerializedName("assigneeId")
    val assigneeId: String?,

    @SerializedName("dueDate")
    val dueDate: String?
)

data class UpdateTaskRequest(
    @SerializedName("title")
    val title: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("status")
    val status: Int?,

    @SerializedName("assigneeId")
    val assigneeId: String?,

    @SerializedName("dueDate")
    val dueDate: String?
)

data class UpdateTaskStatusRequest(
    @SerializedName("status")
    val status: Int
)
