package com.example.serviceexamples.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.serviceexamples.ForegroundServiceActivity
import com.example.serviceexamples.R
import java.time.chrono.HijrahChronology


class ForegroundService : Service() {
    override fun onCreate() {
        super.onCreate()

       /* Handler(Looper.getMainLooper()).postDelayed({
            startForeground(
                2,
                getMyActivityNotification("", completedParts, totalSize)
            )
        }, 500)*/
    }


    //Notification provider
    private fun getMyActivityNotification(
        caption: String,
        completedUnits: Long,
        totalUnits: Long
    ): Notification {
        var percentComplete = 0
        if (totalUnits > 0) {
            percentComplete = (100 * completedUnits / totalUnits).toInt()
        }

        //Return the latest progress of task
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_settings)
            .setContentTitle("Title progress")
            .setContentText(caption)
            .setProgress(100, percentComplete, false)
            .setContentInfo("$percentComplete%")
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }

    /**
     * Show notification with a progress bar.
     * Updating the progress happens here
     */
    private var completedParts = 0L
    private var totalSize = 0L
    private var mCaption = ""
    private var requestCode = 33
    fun showProgressNotification(
        caption: String,
        completedUnits: Long,
        totalUnits: Long,
        code: Int
    ) {
        createNotificationChannel()
        mCaption = caption
        requestCode = code
        completedParts = completedUnits
        totalSize = totalUnits
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(
            requestCode,
            getMyActivityNotification(caption, completedUnits, totalUnits)
        )
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val input = intent.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, ForegroundServiceActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_settings)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        //showProgressNotification("Hiiii", 30L, 100L, 123)

        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
    }
}