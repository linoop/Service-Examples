package com.example.serviceexamples

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.serviceexamples.services.ForegroundService
import com.example.serviceexamples.services.ProgressService


class ForegroundServiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foreground_service)

        findViewById<Button>(R.id.startForegroundService).setOnClickListener {
            startService()
        }
        findViewById<Button>(R.id.stopForegroundService).setOnClickListener {
            stopService()
        }
        findViewById<Button>(R.id.download).setOnClickListener {
            startDownload()
        }
    }

    fun startDownload() {
        val serviceIntent = Intent(this, ProgressService::class.java)
        serviceIntent.putExtra("progress", "Download Service")
        //ContextCompat.startForegroundService(this, serviceIntent)
        startService(serviceIntent)
    }

    fun startService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        serviceIntent.putExtra("inputExtra", "Foreground Service")
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        stopService(serviceIntent)
    }
}