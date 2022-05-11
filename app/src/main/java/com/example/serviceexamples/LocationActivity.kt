package com.example.serviceexamples

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.serviceexamples.services.GPSTrackerService


class LocationActivity : AppCompatActivity() {
    private lateinit var gpsTrackerService: GPSTrackerService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        gpsTrackerService = GPSTrackerService(this)
        findViewById<Button>(R.id.getLocation).setOnClickListener {
            getLocationDetails()
        }
    }

    fun getLocationDetails(){
        if (gpsTrackerService.getIsGPSTrackingEnabled()) {
            val stringLatitude: String = java.lang.String.valueOf(gpsTrackerService.latitude)
            showMessage("latitude $stringLatitude")
            val stringLongitude: String = java.lang.String.valueOf(gpsTrackerService.longitude)
            showMessage("longitude $stringLongitude")
            val country = gpsTrackerService.getCountryName(this)
            showMessage("country $country")
            val city = gpsTrackerService.getLocality(this)
            showMessage("city $city")
            val postalCode = gpsTrackerService.getPostalCode(this)
            showMessage("postalCode $postalCode")
            val addressLine = gpsTrackerService.getAddressLine(this)
            showMessage("addressLine $addressLine")
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTrackerService.showSettingsAlert()
        }
    }

    fun showMessage(message: String) {
        Log.d("SimpleBindServiceLog", message)
    }
}