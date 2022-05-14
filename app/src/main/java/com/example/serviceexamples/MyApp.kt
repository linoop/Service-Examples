package com.example.serviceexamples

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MyApp : Application() {
    companion object{
        const val channelId = "Progress_Notification"
    }

    override fun onCreate(){
        super.onCreate()
        createNotificationChannels()
    }

    //Check if the Android version is greater than 8. (Android Oreo)
    private fun createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Progress Notification",
                //IMPORTANCE_HIGH = shows a notification as peek notification.
                //IMPORTANCE_LOW = shows the notification in the status bar.
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Progress Notification Channel"
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel)
        }
    }
}