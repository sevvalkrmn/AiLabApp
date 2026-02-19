package com.ktun.ailabapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AiLabApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val taskChannel = NotificationChannel(
                "ailab_task",
                "Gorev Bildirimleri",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Yeni gorev atamalari, yaklasan ve gecen due date bildirimleri"
            }

            val announcementChannel = NotificationChannel(
                "ailab_announcement",
                "Duyuru Bildirimleri",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Yeni duyurular ve okunmamis duyuru hatirlaticilari"
            }

            val labChannel = NotificationChannel(
                "ailab_lab",
                "Lab Bildirimleri",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Otomatik cikis uyarilari ve lab ile ilgili bildirimler"
            }

            val defaultChannel = NotificationChannel(
                "ailab_default",
                "Genel Bildirimler",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Diger bildirimler"
            }

            manager.createNotificationChannels(
                listOf(taskChannel, announcementChannel, labChannel, defaultChannel)
            )
        }
    }
}
