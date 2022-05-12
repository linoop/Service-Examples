package com.example.serviceexamples

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.simpleService).setOnClickListener {
            Intent(this, SimpleServiceActivity::class.java).also { startActivity(it) }
        }
        findViewById<Button>(R.id.bindService).setOnClickListener {
            Intent(this, SimpleServiceActivity::class.java).also { startActivity(it) }
        }
    }

}