package com.ktun.ailabapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ktun.ailabapp.MainActivity
import com.ktun.ailabapp.R
import com.ktun.ailabapp.data.remote.api.NotificationApi
import com.ktun.ailabapp.data.remote.dto.request.RegisterFcmTokenRequest
import com.ktun.ailabapp.util.FirebaseAuthManager
import com.ktun.ailabapp.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AilabFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationApi: NotificationApi

    @Inject
    lateinit var authManager: FirebaseAuthManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        const val CHANNEL_TASK = "ailab_task"
        const val CHANNEL_ANNOUNCEMENT = "ailab_announcement"
        const val CHANNEL_LAB = "ailab_lab"
        const val CHANNEL_DEFAULT = "ailab_default"
    }

    /**
     * FCM token yenilendiginde cagirilir.
     * Kullanici login durumundaysa yeni token'i backend'e gonderir.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Logger.d("FCM token yenilendi: ${token.take(20)}...", "FCMService")

        // Kullanici login durumundaysa yeni token'i backend'e gonder
        val authToken = authManager.getTokenSync()
        if (!authToken.isNullOrEmpty()) {
            serviceScope.launch {
                try {
                    notificationApi.registerFcmToken(RegisterFcmTokenRequest(token = token))
                    Logger.d("Yeni FCM token backend'e kaydedildi", "FCMService")
                } catch (e: Exception) {
                    Logger.e("Yeni FCM token kayit hatasi", e, "FCMService")
                }
            }
        }
    }

    /**
     * Bildirim geldiginde cagirilir.
     *
     * Uygulama FOREGROUND'da → Bu metod cagirilir, biz gostermeliyiz.
     * Uygulama BACKGROUND'da VE notification payload varsa → Sistem otomatik gosterir.
     * Uygulama KILLED durumda → Sistem otomatik gosterir.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: return
        val body = message.notification?.body ?: return
        val data = message.data

        val type = data["type"] ?: "default"
        val referenceId = data["referenceId"]

        Logger.d("Bildirim alindi - type: $type, title: $title", "FCMService")

        val channelId = when (type) {
            "task" -> CHANNEL_TASK
            "announcement" -> CHANNEL_ANNOUNCEMENT
            "auto_checkout_warning" -> CHANNEL_LAB
            else -> CHANNEL_DEFAULT
        }

        showNotification(title, body, channelId, type, referenceId)
    }

    private fun showNotification(
        title: String,
        body: String,
        channelId: String,
        type: String,
        referenceId: String?
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_type", type)
            putExtra("reference_id", referenceId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, System.currentTimeMillis().toInt(), intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt()
        manager.notify(notificationId, notification)
    }
}
