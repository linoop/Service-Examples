package com.example.serviceexamples

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    //private lateinit var myService: MyService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //myService = MyService()
        findViewById<Button>(R.id.startService).setOnClickListener {
          Intent(this, MyService::class.java).also {
              startService(it)
          }
        }
    }
}