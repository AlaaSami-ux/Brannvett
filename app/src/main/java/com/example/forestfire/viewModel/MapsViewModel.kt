package com.example.forestfire.viewModel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.media.Image
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.forestfire.R
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

    private var mLocationPermissionGranted = false // assume location permission is not granted

    private lateinit var marker: Marker
    private val norge = LatLngBounds(
        LatLng(58.019156, 2.141567), LatLng(71.399348, 33.442113)
    )
    private val oslo = LatLng(59.911491, 10.757933)
    private var lastUsedLocation: LatLng = oslo
    private var lastUsedLocationName: String = "Oslo"
    private var deviceLoc: LatLng = lastUsedLocation
    private var merInfoVises: Boolean = false

    private lateinit var activity: Activity
    private lateinit var context: Context
    private lateinit var mMap: GoogleMap
    private lateinit var sted1: TextView
    private lateinit var sted2: TextView
    private lateinit var favBtn1: ImageButton
    private lateinit var favBtn2: ImageButton
    private var favorites: MutableList<LatLng> = ArrayList()


    fun setActivity(a: Activity){
        activity = a
    }

    fun setContext(c: Context){
        context = c
    }

    fun setMap(mMap: GoogleMap){
        this.mMap = mMap
    }

    fun setTextViews(v1: TextView, v2: TextView){
        sted1 = v1
        sted2 = v2
    }

    fun setFavButtons(b1: ImageButton, b2: ImageButton){
        favBtn1 =  b1
        favBtn2 =  b2
    }

    fun setCurrentFavorites(favs: MutableList<LatLng>){
        favorites = favs
    }

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
/*
    fun getLastDeviceLocation(){
        // finner siste posisjon funnet for enheten
        Log.d(TAG, "getLastDeviceLocation")
        mFusedLocationProviderClient.lastLocation.addOnSuccessListener {
            // fikk tak i sist kjente lokasjon
            if(it != null && norge.contains(LatLng(it.latitude, it.longitude))){
                Log.d(TAG, "found a device location: " + LatLng(it.latitude, it.longitude).toString())
                deviceLoc = LatLng(it.latitude, it.longitude)
                // fant en lokasjon. Flytter kartet dit
                Log.d(TAG, "flytter kamera og legger på markør")
                moveCam(deviceLoc)
                addMarker(deviceLoc)
                // oppdaterer sist brukte posisjon
                Log.d(TAG, "oppdaterer sist brukte posisjon til $deviceLoc")
                setLastUsedLocation(deviceLoc)
                Log.d(TAG, "oppdaterer stedsnavn")
                sted1.text = getAddressFromLocation(deviceLoc.latitude, deviceLoc.longitude)
                sted2.text = sted1.text
                if (favorites.contains(deviceLoc)){
                    Log.d(TAG, "the device location is already a favorite")
                    favBtn1.setBackgroundResource(R.drawable.ic_favorite_black_24dp)
                    favBtn2.setBackgroundResource(R.drawable.ic_favorite_black_24dp)
                } else {
                    favBtn1.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp)
                    favBtn2.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp)
                    Log.d(TAG, "the device location is not a favorite")
                }
            }
        }
    }
 */

    fun moveCam(ll: LatLng) {
        //lastUsedLocation = ll
        Log.d(TAG, "moveCam to $ll")
        mMap.setLatLngBoundsForCameraTarget(norge)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, DEFAULT_ZOOM))
    }

    fun addMarker(ll: LatLng) {
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

    fun setMerInfoVises(b: Boolean){
        Log.d(TAG, "mer info vises settes til $b")
        merInfoVises = b
    }

    fun getMerInfoVises(): Boolean{
        Log.d(TAG, "mer info er $merInfoVises")
        return merInfoVises
    }
}