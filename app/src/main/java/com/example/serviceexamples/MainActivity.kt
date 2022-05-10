package com.example.serviceexamples

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivityLog"
    //private lateinit var bindService: BindService
    private lateinit var localBinder: BindService.LocalBinder
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            //val binder = service as BindService.LocalBinder
            //bindService = binder.getService()
            localBinder = service as BindService.LocalBinder
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, BindService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    fun onBindServiceClick() {
        if (mBound) {
            //val name: String = bindService.getName()
            val result = localBinder.getAddress()
            showMessage(result)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.startService).setOnClickListener {
          Intent(this, MyService::class.java).also {
              it.putExtra("name", "linoop")
              startService(it)
          }
        }

        findViewById<Button>(R.id.bindService).setOnClickListener {
            onBindServiceClick()
        }
    }

    private fun showMessage(message: String) {
        Log.d(TAG, message)
    }
}