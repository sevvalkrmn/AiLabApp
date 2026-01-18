package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class TaskHistoryItem(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("status") val status: Int, // 0: Todo, 1: InProgress, 2: Done
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("assigneeId") val assigneeId: String,
    @SerializedName("assigneeName") val assigneeName: String?,
    @SerializedName("projectId") val projectId: String,
    @SerializedName("projectName") val projectName: String?
)

// UI Model
data class TaskHistory(
    val id: String,
    val title: String,
    val status: TaskStatus,
    val createdAt: String,
    val projectName: String
)

enum class TaskStatus(val value: Int, val label: String, val color: Long) {
    TODO(0, "Yapılacak", 0xFF9E9E9E),
    IN_PROGRESS(1, "Devam Ediyor", 0xFFFF9800),
    DONE(2, "Tamamlandı", 0xFF4CAF50);

    companion object {
        fun fromInt(value: Int): TaskStatus {
            return values().find { it.value == value } ?: TODO
        }
    }
}

// Mapper
fun TaskHistoryItem.toTaskHistory(): TaskHistory {
    return TaskHistory(
        id = this.id,
        title = this.title,
        status = TaskStatus.fromInt(this.status),
        createdAt = formatDate(this.createdAt),
        projectName = this.projectName ?: "Proje Yok"
    )
}

private fun formatDate(isoDate: String): String {
    return try {
        val zonedDateTime = java.time.ZonedDateTime.parse(isoDate)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy", java.util.Locale("tr"))
        zonedDateTime.format(formatter)
    } catch (e: Exception) {
        isoDate
    }
}