package com.example.forestfire.viewModel

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsViewModel(): ViewModel(){ //AndroidViewModel(app)
    private var DEFAULT_ZOOM = 15f

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var deviceLoc: LatLng? = null
    private var chosenLoc: LatLng? = null
    private lateinit var marker: Marker

    fun getDeviceLocation(mMap: GoogleMap, applicationContext: Context): LatLng? {
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)
        mFusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                deviceLoc = LatLng(location.latitude, location.longitude)
                moveCam(mMap, applicationContext, LatLng(location.latitude, location.longitude), DEFAULT_ZOOM)
            } else {
                Toast.makeText(applicationContext, "could not get location", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return deviceLoc
    }

    fun moveCam(mMap: GoogleMap, applicationContext: Context, ll: LatLng?, zoom: Float) {
        if (ll == null) {
            Toast.makeText(applicationContext, "kan ikke flytte kamera", Toast.LENGTH_SHORT).show()
        } else {
            chosenLoc = ll
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, zoom))
        }
    }

    fun addMarker(mMap: GoogleMap, ll: LatLng?) {
        if (ll != null) {
            if (::marker.isInitialized) {
                marker.remove()
            }
            marker = mMap.addMarker(MarkerOptions().position(ll).title("markør plassering"))
        }
    }

    fun getChosenLocation(): LatLng? {
        return chosenLoc
    }
}