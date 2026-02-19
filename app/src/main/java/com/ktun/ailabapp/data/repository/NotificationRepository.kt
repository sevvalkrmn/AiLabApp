package com.ktun.ailabapp.data.repository

import com.google.firebase.messaging.FirebaseMessaging
import com.ktun.ailabapp.data.remote.api.NotificationApi
import com.ktun.ailabapp.data.remote.dto.request.RegisterFcmTokenRequest
import com.ktun.ailabapp.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationApi: NotificationApi
) {

    /**
     * FCM token'i alir ve backend'e kaydeder.
     * Login basarili olduktan sonra cagrilmalidir.
     */
    suspend fun registerFcmToken() = withContext(Dispatchers.IO) {
        try {
            val fcmToken = FirebaseMessaging.getInstance().token.await()
            Logger.d("FCM Token alindi: ${fcmToken.take(20)}...", "NotificationRepo")

            val response = notificationApi.registerFcmToken(RegisterFcmTokenRequest(token = fcmToken))
            if (response.isSuccessful) {
                Logger.d("FCM token backend'e kaydedildi", "NotificationRepo")
            } else {
                Logger.e("FCM token kayit hatasi: ${response.code()}", tag = "NotificationRepo")
            }
        } catch (e: Exception) {
            Logger.e("FCM token kayit hatasi", e, "NotificationRepo")
        }
    }

    /**
     * Backend'den FCM token'i siler.
     * Logout sirasinda cagrilmalidir.
     */
    suspend fun unregisterFcmToken() = withContext(Dispatchers.IO) {
        try {
            val response = notificationApi.unregisterFcmToken()
            if (response.isSuccessful) {
                Logger.d("FCM token backend'den silindi", "NotificationRepo")
            } else {
                Logger.e("FCM token silme hatasi: ${response.code()}", tag = "NotificationRepo")
            }
        } catch (e: Exception) {
            Logger.e("FCM token silme hatasi", e, "NotificationRepo")
        }

        // Lokal FCM token'i da sil
        try {
            FirebaseMessaging.getInstance().deleteToken().await()
            Logger.d("Lokal FCM token silindi", "NotificationRepo")
        } catch (e: Exception) {
            Logger.e("Lokal FCM token silme hatasi", e, "NotificationRepo")
        }
    }
}
