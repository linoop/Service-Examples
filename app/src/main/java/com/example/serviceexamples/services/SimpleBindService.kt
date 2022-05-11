package com.example.serviceexamples.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import kotlin.concurrent.thread

class SimpleBindService : Service() {
    inner class LocalBinder : Binder() {
        fun getService(): SimpleBindService = this@SimpleBindService
        fun printCount() {
            var i = 0
            thread {
                while (true) {
                    Thread.sleep(1000)
                    showMessage("Count $i")
                    i++
                }
            }
        }
    }

    private val binder = LocalBinder()

    fun getDetails(): String {
        return "Linoop"
    }

    override fun onBind(p0: Intent?): IBinder {
        val message = p0?.getStringExtra("name")
        showMessage(message.toString())
        return binder
    }


    fun showMessage(message: String) {
        Log.d("SimpleBindServiceLog", message)
    }
}