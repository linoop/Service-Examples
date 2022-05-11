package com.example.serviceexamples

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.TextView
import com.example.serviceexamples.services.MyService
import com.example.serviceexamples.services.SimpleBindService

class MainActivity : AppCompatActivity() {
    private lateinit var service: SimpleBindService
    private var bound = false
    private lateinit var message: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        message = findViewById(R.id.message)

        findViewById<Button>(R.id.startService).setOnClickListener {
            Intent(this, MyService::class.java).also {
                startService(it)
            }
        }

        findViewById<Button>(R.id.bindService).setOnClickListener {
            getServiceDetails()
        }
        findViewById<Button>(R.id.gotoMessenger).setOnClickListener {
           Intent(this, MessengerActivity::class.java).also {
               startActivity(it)
           }
        }

        findViewById<Button>(R.id.startCounter).setOnClickListener {
            service.printNumber()
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, SimpleBindService::class.java).also {
            it.putExtra("name", "name linoop")
            bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        bound = false
    }

    private fun getServiceDetails() {
        if (bound) {
            val details = service.getDetails()
            message.text = details
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, iBinder: IBinder?) {
            val binder = iBinder as SimpleBindService.LocalBinder
            service = binder.getService()
            bound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            bound = false
        }
    }
}