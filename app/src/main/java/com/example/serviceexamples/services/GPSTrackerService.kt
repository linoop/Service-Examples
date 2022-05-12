package com.example.serviceexamples.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.serviceexamples.R
import kotlinx.coroutines.NonCancellable.cancel
import java.io.IOException
import java.util.*


class GPSTrackerService : Service(), LocationListener {
    // Get Class Name
    private val TAG: String = GPSTrackerService::class.java.name

    //private var mContext: Context? = null
    init {
        getLocation()
    }

    // flag for GPS Status
    var isGPSEnabled = false

    // flag for network status
    var isNetworkEnabled = false

    // flag for GPS Tracking is enabled
    var isGPSTrackingEnabled = false

    var location: Location? = null
    var latitude = 0.0
    var longitude = 0.0

    // How many Geocoder should return our GPSTracker
    var geocoderMaxResults = 1

    // The minimum distance to change updates in meters
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters


    // The minimum time between updates in milliseconds
    private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute

    // Declaring a Location Manager
    private lateinit var locationManager: LocationManager

    // Store LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER information
    private lateinit var provider_info: String

    /**
     * Try to get my current location by GPS or Network Provider
     */
    fun getLocation() {
        try {
            locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager

            //getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            //getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            // Try to get location if you GPS Service is enabled
            if (isGPSEnabled) {
                isGPSTrackingEnabled = true
                Log.d(TAG, "Application use GPS Service")

                /*
                 * This provider determines location using
                 * satellites. Depending on conditions, this provider may take a while to return
                 * a location fix.
                 */
                provider_info = LocationManager.GPS_PROVIDER
            } else if (isNetworkEnabled) { // Try to get location if you Network Service is enabled
                isGPSTrackingEnabled = true
                Log.d(TAG, "Application use Network State to get GPS coordinates")

                /*
                 * This provider determines location based on
                 * availability of cell tower and WiFi access points. Results are retrieved
                 * by means of a network lookup.
                 */
                provider_info = LocationManager.NETWORK_PROVIDER
            }

            // Application can use GPS or Network Provider
            if (provider_info.isNotEmpty()) {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                locationManager.requestLocationUpdates(
                    provider_info,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                    this
                )
                //location = locationManager.getLastKnownLocation(provider_info)
                location = getLastKnownLocation(locationManager)
                updateGPSCoordinates()
            }
        } catch (e: Exception) {
            //e.printStackTrace();
            Log.e(TAG, "Impossible to connect to LocationManager", e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(mLocationManager:LocationManager): Location? {
        //val mLocationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        val providers: List<String> = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l: Location = mLocationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        return bestLocation
    }

    /**
     * Update GPSTracker latitude and longitude
     */
    fun updateGPSCoordinates() {
        if (location != null) {
            latitude = location!!.latitude
            longitude = location!!.longitude
        }
    }

    /**
     * GPSTracker latitude getter and setter
     * @return latitude
     */
    fun getLocLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }
        return latitude
    }

    /**
     * GPSTracker longitude getter and setter
     * @return
     */
    fun getLocLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }
        return longitude
    }

    /**
     * GPSTracker isGPSTrackingEnabled getter.
     * Check GPS/wifi is enabled
     */
    fun getIsGPSTrackingEnabled(): Boolean {
        return isGPSTrackingEnabled
    }

    /**
     * Stop using GPS listener
     * Calling this method will stop using GPS in your app
     */
    fun stopUsingGPS() {
        /*if (locationManager != null) {
            //locationManager.removeUpdates(this@GPSTrackerService)
            locationManager!!.removeUpdates(this)
        }*/
        locationManager.removeUpdates(this)
    }

    /**
     * Function to show settings alert dialog
     */
    fun showSettingsAlert(context: Context) {
        val alertDialog = AlertDialog.Builder(context)

        //Setting Dialog Title
        alertDialog.setTitle("GPS Settings")

        //Setting Dialog Message
        alertDialog.setMessage("Please enable gps")

        //On Pressing Setting button
        alertDialog.setPositiveButton("Ok") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }

        //On pressing cancel button
        alertDialog.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    /**
     * Get list of address by latitude and longitude
     * @return null or List<Address>
    </Address> */
    fun getGeocoderAddress(context: Context?): List<Address>? {
        if (location != null) {
            val geocoder = Geocoder(context, Locale.ENGLISH)
            try {
                return geocoder.getFromLocation(latitude, longitude, geocoderMaxResults)
            } catch (e: IOException) {
                //e.printStackTrace();
                Log.e(TAG, "Impossible to connect to Geocoder", e)
            }
        }
        return null
    }

    /**
     * Try to get AddressLine
     * @return null or addressLine
     */
    fun getAddressLine(context: Context?): String? {
        val addresses: List<Address>? = getGeocoderAddress(context)
        return if (addresses != null && addresses.isNotEmpty()) {
            val address: Address = addresses[0]
            address.getAddressLine(0)
        } else {
            null
        }
    }

    /**
     * Try to get Locality
     * @return null or locality
     */
    fun getLocality(context: Context?): String? {
        val addresses: List<Address>? = getGeocoderAddress(context)
        return if (addresses != null && addresses.isNotEmpty()) {
            val address: Address = addresses[0]
            address.locality
        } else {
            null
        }
    }

    /**
     * Try to get Postal Code
     * @return null or postalCode
     */
    fun getPostalCode(context: Context?): String? {
        val addresses: List<Address>? = getGeocoderAddress(context)
        return if (addresses != null && addresses.isNotEmpty()) {
            val address: Address = addresses[0]
            address.postalCode
        } else {
            null
        }
    }

    /**
     * Try to get CountryName
     * @return null or postalCode
     */
    fun getCountryName(context: Context?): String? {
        val addresses: List<Address>? = getGeocoderAddress(context)
        return if (addresses != null && addresses.isNotEmpty()) {
            val address: Address = addresses[0]
            address.countryName
        } else {
            null
        }
    }

    override fun onLocationChanged(p0: Location) {
        TODO("Not yet implemented")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}