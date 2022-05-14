package com.example.serviceexamples

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.simpleService).setOnClickListener {
            Intent(this, SimpleServiceActivity::class.java).also { startActivity(it) }
        }
        findViewById<Button>(R.id.boundService).setOnClickListener {
            Intent(this, BoundServiceActivity::class.java).also { startActivity(it) }
        }
        findViewById<Button>(R.id.foregroundService).setOnClickListener {
            Intent(this, ForegroundServiceActivity::class.java).also { startActivity(it) }
        }
    }

}