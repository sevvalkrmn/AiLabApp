package com.ktun.ailabapp.presentation.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun sendFeedbackEmail(
    context: Context,
    feedback: String,
    onSuccess: () -> Unit = {}
) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("ktunailab@gmail.com"))  // ← LAB MAİLİNİZİ BURAYA YAZIN
            putExtra(Intent.EXTRA_SUBJECT, "AI Lab App - Hata Bildirimi")
            putExtra(Intent.EXTRA_TEXT, """
                |=== HATA BİLDİRİMİ ===
                |
                |$feedback
                |
                |--- Cihaz Bilgileri ---
                |Model: ${android.os.Build.MODEL}
                |Android: ${android.os.Build.VERSION.RELEASE}
                |Tarih: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}
            """.trimMargin())
        }

        context.startActivity(Intent.createChooser(intent, "Email Uygulaması Seç"))
        onSuccess()
    } catch (e: Exception) {
        Toast.makeText(context, "Email uygulaması bulunamadı", Toast.LENGTH_SHORT).show()
    }
}