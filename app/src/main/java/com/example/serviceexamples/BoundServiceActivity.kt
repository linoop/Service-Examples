package com.example.serviceexamples

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.serviceexamples.services.MessengerService
import com.example.serviceexamples.services.MessengerService.Companion.MSG_REGISTER_CLIENT
import com.example.serviceexamples.services.MessengerService.Companion.MSG_SET_VALUE
import com.example.serviceexamples.services.MessengerService.Companion.MSG_UNREGISTER_CLIENT


class BoundServiceActivity : AppCompatActivity() {
    private lateinit var mCallbackText: TextView

    private lateinit var mService: Messenger
    private var bound: Boolean = false
    val mMessenger: Messenger = Messenger(IncomingHandler())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bound_service)
        mCallbackText = findViewById(R.id.message)
        findViewById<Button>(R.id.bindService).setOnClickListener {
            doBindService()
        }
        findViewById<Button>(R.id.unbindService).setOnClickListener {
            doUnbindService()
        }
    }


    inner class IncomingHandler : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_SET_VALUE -> mCallbackText.text = "Received from service: " + msg.arg1
                else -> super.handleMessage(msg)
            }
        }
    }

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mService = Messenger(service)
            mCallbackText.text = "Attached."
            try {
                var msg = Message.obtain(null, MSG_REGISTER_CLIENT)
                msg.replyTo = mMessenger
                mService.send(msg)
                msg = Message.obtain(
                    null,
                    MSG_SET_VALUE, this.hashCode(), 0
                )
                mService.send(msg)
            } catch (e: RemoteException) {

            }

            // As part of the sample, tell the user what happened.

            // As part of the sample, tell the user what happened.
            Toast.makeText(
                this@BoundServiceActivity,
                "remote_service_connected",
                Toast.LENGTH_SHORT
            ).show()

        }

        override fun onServiceDisconnected(className: ComponentName) {
            // mService = null
            mCallbackText.text = "Disconnected.";
            // As part of the sample, tell the user what happened.
            Toast.makeText(
                this@BoundServiceActivity,
                "remote_service_disconnected",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun doBindService() {
        bindService(
            Intent(
                this@BoundServiceActivity,
                MessengerService::class.java
            ), mConnection, Context.BIND_AUTO_CREATE
        )
        bound = true
        mCallbackText.text = "Binding."
    }

    fun doUnbindService() {
        if (bound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null) {
                try {
                    val msg = Message.obtain(
                        null,
                        MSG_UNREGISTER_CLIENT
                    )
                    msg.replyTo = mMessenger
                    mService.send(msg)
                } catch (e: RemoteException) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }

            // Detach our existing connection.
            unbindService(mConnection)
            bound = false
            mCallbackText.text = "Unbinding."
        }
    }



}