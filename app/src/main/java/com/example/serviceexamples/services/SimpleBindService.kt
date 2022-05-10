package com.example.serviceexamples.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class SimpleBindService : Service() {
    private val binder = LocalBinder()
    fun getDetails(): String {
        return "Linoop"
    }

    inner class LocalBinder : Binder() {
        fun getService(): SimpleBindService = this@SimpleBindService
    }

    override fun onBind(p0: Intent?): IBinder {
        val message = p0?.getStringExtra("name")
        showMessage(message.toString())
        return binder
    }

    private fun showMessage(message: String) {
        Log.d("SimpleBindServiceLog", message)
    }
}