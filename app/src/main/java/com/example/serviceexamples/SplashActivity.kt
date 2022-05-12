package com.example.serviceexamples

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat

class SplashActivity : AppCompatActivity() {
    private var hasReadPermission = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkPermission()
    }


    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun checkPermission() {
        if (hasPermissions(*permissions)) {
            hasReadPermission = true
            startMainActivity()
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                PERMISSION_CHECK_REQ_CODE
            )
        }
    }

    private fun startMainActivity() {
        Intent(this, HomeActivity::class.java).also { startActivity(it) }
    }

    private fun hasPermissions(vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CHECK_REQ_CODE -> {
                hasReadPermission = grantResults.isNotEmpty() && hasPermissions(*permissions)
                if (hasReadPermission)
                    startMainActivity()
            }
        }
    }

    companion object {
        const val PERMISSION_CHECK_REQ_CODE = 123
    }
}