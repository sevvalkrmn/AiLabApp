package com.ktun.ailabapp.util

import androidx.compose.ui.graphics.Color
import com.ktun.ailabapp.ui.theme.InfoBlue
import com.ktun.ailabapp.ui.theme.SuccessGreen
import com.ktun.ailabapp.ui.theme.WarningOrange
import java.text.SimpleDateFormat
import java.util.*

fun getStatusColor(status: String): Color {
    return when (status) {
        "Todo" -> WarningOrange
        "InProgress" -> InfoBlue
        "Done" -> SuccessGreen
        else -> Color.Gray
    }
}

fun getStatusText(status: String): String {
    return when (status) {
        "Todo" -> "Yapılacak"
        "InProgress" -> "Devam Ediyor"
        "Done" -> "Tamamlandı"
        else -> status
    }
}

fun formatDate(dateString: String): String {
    if (dateString.isEmpty()) return "Tarih yok"

    return try {
        // Backend'den gelen format: 2025-10-23T10:51:01.486848Z
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("tr"))
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        try {
            // Alternatif format: 2025-10-23T10:51:01
            val inputFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("tr"))
            val date = inputFormat2.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e2: Exception) {
            dateString
        }
    }
}