package com.example.serviceexamples

import android.R
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log


class LocalService : Service() {

    companion object {
        const val TAG = "LocalServiceLog"
        const val NOTIFICATION = 1020
    }

    private lateinit var notificationManager: NotificationManager
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): LocalService = this@LocalService
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        showNotification()
        Log.d(TAG,"on Create")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand-Received start id $startId: $intent")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        notificationManager.cancel(NOTIFICATION)
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    private fun showNotification() {
        val contentIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MessageActivity::class.java), 0
        )
        // Set the info for the views that show in the notification panel.
        val notification: Notification = Notification.Builder(this)
            .setSmallIcon(R.drawable.ic_menu_set_as)
            .setTicker("Hi")
            .setWhen(System.currentTimeMillis())
            .setContentTitle("Content title...")
            .setContentText("Hii")
            .setContentIntent(contentIntent)
            .build()
        notificationManager.notify(NOTIFICATION, notification)
    }
}