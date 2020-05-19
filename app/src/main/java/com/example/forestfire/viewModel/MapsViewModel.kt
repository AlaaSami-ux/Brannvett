package com.example.forestfire.viewModel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*

class MapsViewModel(): ViewModel(){ //AndroidViewModel(app)
    val TAG = "MapsViewModel"
    private var DEFAULT_ZOOM = 15f
    private var MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1
    private var MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var mLocationPermissionGranted = false // assume location permission is not granted

    private lateinit var marker: Marker
    private val norge = LatLngBounds(
        LatLng(58.019156, 2.141567), LatLng(71.399348, 33.442113)
    )
    private val oslo = LatLng(59.911491, 10.757933)
    private var lastUsedLocation: LatLng = oslo
    private var lastUsedLocationName: String = "Oslo"
    private var deviceLoc: LatLng = oslo

    private lateinit var activity: Activity
    private lateinit var context: Context

    fun setFusedLocationProviderClient(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    fun setActivity(a: Activity){
        activity = a
    }

    fun setContext(c: Context){
        context = c
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
    }

    /*fun getDeviceLocation(mMap: GoogleMap): LatLng {
        Log.d(TAG, "getDeviceLocation called")
        findDeviceLocation(mMap)
        Log.d(TAG, "After findDeviceLocation called")
        return deviceLoc
    }

    fun findDeviceLocation(mMap: GoogleMap){
        Log.d(TAG, "findDeviceLocation called")
        mFusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                Log.d(TAG, "found a location")
                deviceLoc = LatLng(location.latitude, location.longitude)
                if (!norge.contains(deviceLoc)){ // enheten befinner seg ikke i norge
                    Log.d(TAG, "location outside of Norway")
                    Toast.makeText(context,
                        "Din posisjon er utenfor Norge. Søk på et sted i Norge",
                        Toast.LENGTH_LONG).show()
                } else {
                    Log.d(TAG, "location in Norway")
                    deviceLoc = LatLng(location.latitude, location.longitude)
                }
            } else {
                moveCam(mMap, oslo)
                Toast.makeText(context, "kunne ikke finne posisjonen din", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }*/

    fun getLocationPermission(){
        // get permission to get current location
        Log.d(TAG, "getLocationPermission called")
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "permission fine and coarse location granted")
                mLocationPermissionGranted = true
            } else {
                Log.d(TAG, "permission COARSE_LOCATION not granted")
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION
                )
            }
        } else {
            Log.d(TAG, "permission FINE_LOCATION not granted")
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                MY_PERMISSIONS_REQUEST_FINE_LOCATION
            )
        }
    }

    fun getUserLocation(): LatLng?{
        Log.d(TAG, "getUserLocation called")
        var sted: LatLng? = null
        mFusedLocationProviderClient.lastLocation.addOnSuccessListener {
            // kan bli null
            if (it != null){
                sted = LatLng(it.latitude, it.longitude)
                if (!norge.contains(sted)){
                    sted = null
                }
            }
        }
        return sted
    }

    fun moveCam(mMap: GoogleMap, ll: LatLng) {
        //lastUsedLocation = ll
        Log.d(TAG, "moveCam to $ll")
        mMap.setLatLngBoundsForCameraTarget(norge)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, DEFAULT_ZOOM))
    }

    fun addMarker(mMap: GoogleMap, ll: LatLng) {
        Log.d(TAG, "add marker to $ll")
        if (::marker.isInitialized) {
            marker.remove()
        }
        marker = mMap.addMarker(MarkerOptions().position(ll).title("markør plassering"))
    }

    fun getAddressFromLocation(latitude: Double, longitude: Double) : String {
        Log.d(TAG, "getAddressFromLocation")
        var sted = "Valgt posisjon"
        val geocoder = Geocoder(activity, Locale.ENGLISH)
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.isNotEmpty()) {
                val fetchedAddress: Address = addresses[0]
                val strAddress: String = fetchedAddress.getAddressLine(0)
                sted = strAddress.split(",", ignoreCase=true, limit=0).first()
                Log.d(TAG, "sted: $sted")
            } else {
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sted
    }

    fun setLastUsedLocation(ll: LatLng, s: String = getAddressFromLocation(ll.latitude
    , ll.longitude)){
        Log.d(TAG, "setLastUsedLocation to $ll")
        lastUsedLocation = ll
        lastUsedLocationName = s
    }

    fun getLastUsedLocation(): LatLng{
        return lastUsedLocation
    }

    fun getLastUsedLocationName(): String{
        return lastUsedLocationName
    }
}