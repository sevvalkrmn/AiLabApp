package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

/**
 * Yeni görev oluşturma isteği
 * POST /api/tasks
 */
data class CreateTaskRequest(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("projectId")
    val projectId: String,

    @SerializedName("assignedToUserId")
    val assignedToUserId: String? = null,

    @SerializedName("dueDate")
    val dueDate: String? = null
)

/**
 * Görev güncelleme isteği
 * PUT /api/tasks/{id}
 */
data class UpdateTaskRequest(
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("assignedToUserId")
    val assignedToUserId: String? = null,

    @SerializedName("dueDate")
    val dueDate: String? = null
)

/**
 * Görev durumu güncelleme isteği
 * PUT /api/tasks/{id}/status
 */
data class UpdateTaskStatusRequest(
    @SerializedName("status")
    val status: Int  // 0=Todo, 1=InProgress, 2=Done
)