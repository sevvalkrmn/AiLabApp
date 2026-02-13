package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class TaskResponse(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("title")
    val title: String = "İsimsiz Görev",

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("status")
    public val _status: Int = 0,  // ← Backend'den 0, 1, 2 geliyor

    @SerializedName("projectId")
    val projectId: String? = null,

    @SerializedName("projectName")
    val projectName: String = "Proje",

    @SerializedName("assignedTo")
    val assignedTo: AssignedUser? = null,

    @SerializedName("assigneeName")
    val assigneeName: String? = null,

    @SerializedName("dueDate")
    val dueDate: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    @SerializedName("score")
    val score: Double? = null // ✅ YENİ: Görev puanı
) {
    // Status'u String'e çevir
    val status: String
        get() = when (_status) {
            0 -> "Todo"
            1 -> "InProgress"
            2 -> "Done"
            else -> "Todo"
        }

    // UI'da kullanmak için renk
    val statusColor: androidx.compose.ui.graphics.Color
        get() = when (_status) {
            0 -> com.ktun.ailabapp.ui.theme.WarningOrange // Turuncu
            1 -> com.ktun.ailabapp.ui.theme.InfoBlue // Mavi
            2 -> com.ktun.ailabapp.ui.theme.SuccessGreen // Yeşil
            else -> androidx.compose.ui.graphics.Color.Gray
        }
}

data class AssignedUser(
    @SerializedName("userId")
    val userId: String = "",

    @SerializedName("fullName")
    val fullName: String = "İsimsiz",

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null
)