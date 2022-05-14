package com.example.serviceexamples.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.example.serviceexamples.ForegroundServiceActivity
import com.example.serviceexamples.MyApp.Companion.channelId
import com.example.serviceexamples.R

class ProgressService : Service() {
    lateinit var notificationManager: NotificationManager
    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    "channel_id",
                    "channel_name",
                    NotificationManager.IMPORTANCE_HIGH
                )
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern =
                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, ForegroundServiceActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )




        //Sets the maximum progress as 100
        val progressMax = 100
        //Creating a notification and setting its various attributes
        val notification =
            NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_settings)
                .setContentTitle("Prog...")
                .setContentText("Downloading...")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(progressMax, 0, true)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)


       // startForeground(4, notification.build())

        //Initial Alert
        notificationManager.notify(4, notification.build())

        Thread {
            SystemClock.sleep(2000)
            var progress = 0
            while (progress <= progressMax) {
                SystemClock.sleep(1000)
                progress += 1
                //Use this to make it a Fixed-duration progress indicator notification

                notification.setContentText("$progress%")
                    .setProgress(progressMax, progress, false)

                notificationManager.notify(4, notification.build())
            }

            notification.setContentText("Download complete")
                .setProgress(0, 0, false)
                .setOngoing(false)
            notificationManager.notify(4, notification.build())
            stopSelf()
        }.start()

        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}