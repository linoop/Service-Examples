package com.example.serviceexamples

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class BindService : Service() {

    private val binder = LocalBinder()

    fun getName(): String {
        return "linoop"
    }
    inner class LocalBinder : Binder() {
       // fun getService(): BindService = this@BindService
        fun getAddress() = "Kakkanad"
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }
}