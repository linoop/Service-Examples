package com.example.serviceexamples.services

import android.app.*
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import com.example.serviceexamples.R
import com.example.serviceexamples.SimpleServiceActivity
import kotlin.concurrent.thread


class MessengerService : Service() {
    var notificationManager: NotificationManager? = null
    var mClients = ArrayList<Messenger>()
    var mValue = 0
    private val mMessenger: Messenger = Messenger(IncomingHandler())

    internal inner class IncomingHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_REGISTER_CLIENT -> mClients.add(msg.replyTo)
                MSG_UNREGISTER_CLIENT -> mClients.remove(msg.replyTo)
                MSG_SET_VALUE -> {
                    mValue = msg.arg1
                    val request = msg.data.getString("data")
                    Log.d(TAG, "request $request")
                    var i = mClients.size - 1
                    while (i >= 0) {
                        try {
                            mClients[i].send(
                                Message.obtain(
                                    null,
                                    MSG_SET_VALUE, mValue, 0
                                )
                            )
                        } catch (e: RemoteException) {
                            mClients.removeAt(i)
                        }
                        i--
                    }
                }
                START_COUNTER -> startCounter()
                else -> super.handleMessage(msg)
            }
        }
    }

    private fun startCounter() {
        Log.d(TAG, "Counter started")
        var i = mClients.size - 1
        thread {
            while (i >= 0) {
                try {
                    var count = 0
                    while (count < 10) {
                        val msg = Message.obtain(null, START_COUNTER, 0, 0)
                        msg.data.putString("count", count.toString())
                        mClients[i].send(msg)
                        count++
                        Thread.sleep(1000)
                    }
                } catch (e: RemoteException) {
                    mClients.removeAt(i)
                }
                i--
            }
        }
    }

    override fun onCreate() {
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
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
        showNotification()
    }

    override fun onDestroy() {
        notificationManager!!.cancel(R.string.service_stopped)
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show()
    }

    override fun onBind(intent: Intent): IBinder? {
        return mMessenger.binder
    }

    private fun showNotification() {
        val intent = Intent(this, SimpleServiceActivity::class.java)
        val contentIntent = PendingIntent.getActivity(
            this, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Set the info for the views that show in the notification panel.
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_settings) // the status icon
                .setTicker("Hi") // the status text
                .setWhen(System.currentTimeMillis()) // the time stamp
                .setContentTitle("Content title") // the label of the entry
                .setContentText("Hii") // the contents of the entry
                .setContentIntent(contentIntent) // The intent to send when the entry is clicked
                .build()
        } else {
            Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_settings) // the status icon
                .setTicker("Hi") // the status text
                .setWhen(System.currentTimeMillis()) // the time stamp
                .setContentTitle("Content title") // the label of the entry
                .setContentText("Hii") // the contents of the entry
                .setContentIntent(contentIntent) // The intent to send when the entry is clicked
                .build()
        }
        notificationManager!!.notify(R.string.service_started, notification)
    }

    companion object {
        const val MSG_REGISTER_CLIENT = 1
        const val MSG_UNREGISTER_CLIENT = 2
        const val MSG_SET_VALUE = 3
        const val START_COUNTER = 4
        private const val TAG = "MessengerServiceLog"
    }
}