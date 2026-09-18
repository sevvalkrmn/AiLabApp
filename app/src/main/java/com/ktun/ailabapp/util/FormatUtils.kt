package com.ktun.ailabapp.util

import java.text.SimpleDateFormat
import java.util.*

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