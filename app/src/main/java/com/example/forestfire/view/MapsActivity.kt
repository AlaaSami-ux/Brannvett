package com.example.forestfire.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MotionEventCompat
import com.example.forestfire.R
import com.example.forestfire.viewModel.MapsViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback,
    View.OnTouchListener{

    val TAG = "MapsActivity"
    private var DEFAULT_ZOOM = 15f
    private var MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1
    private var MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1
    private var MIN_DISTANCE = 100

    private lateinit var mMap: GoogleMap
    private lateinit var marker: Marker
    private var deviceloc: LatLng? = null
    private var chosenLoc: LatLng? = null
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    //private var previousX: Float = 0F
    private var previousY: Float = 0F

    private lateinit var slideUp: CardView
    private var mLocationPermissionGranted = false
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    val mapsViewModel: MapsViewModel by viewModels()
    //private val mapsViewModel: AndroidViewModel
    //private val mapsViewModel: AndroidViewModel = ViewModelProvider(this@MapsActivity).get(MapsViewModel::class.java)


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        slideUp = findViewById<CardView>(R.id.slideUp)
        slideUp.setOnTouchListener(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // initialize Places
        Places.initialize(applicationContext, "AIzaSyD10fJ7iHSaVhairAHZnpuFcrm5fU4SFM4")
        // create a Places client instance
        var placesClient = Places.createClient(this)
        // Initialize the AutocompleteSupportFragment
        autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )
        // set bounds for the results
        autocompleteFragment.setLocationBias(
            RectangularBounds.newInstance(
                LatLngBounds(
                    LatLng(58.019156, 2.141567), LatLng(71.399348, 33.442113)
                )
            )
        )
        autocompleteFragment.setCountries("NO")
        autocompleteFragment.setActivityMode(AutocompleteActivityMode.OVERLAY)
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object :
            PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(TAG, "Place: " + place.name + ", " + place.id)
                mapsViewModel.moveCam(mMap, applicationContext, place.latLng, DEFAULT_ZOOM)
                mapsViewModel.addMarker(mMap, place.latLng)
            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
                Toast.makeText(applicationContext, "Det har skjedd en feil", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        getLocationPermission()
    }

    private fun getLocationPermission() {
        Log.d(TAG, "getLocationPermission called")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "permission fine and coarse location granted")
                mLocationPermissionGranted = true
            } else {
                Log.d(TAG, "permission COARSE_LOCATION not granted")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION
                )
            }
        } else {
            Log.d(TAG, "permission FINE_LOCATION not granted")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                MY_PERMISSIONS_REQUEST_FINE_LOCATION
            )
        }
    }

    /*
    // denne er lagt til i MapsViewModel
    private fun getDeviceLocation(): LatLng? {
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)
        mFusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                deviceloc = LatLng(location.latitude, location.longitude)
                moveCam(LatLng(location.latitude, location.longitude), DEFAULT_ZOOM)
            } else {
                Toast.makeText(applicationContext, "could not get location", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return deviceloc
    }

    // Denne er lagt til i MapsViewModel
    private fun moveCam(ll: LatLng?, zoom: Float) {
        if (ll == null) {
            Toast.makeText(applicationContext, "kan ikke flytte kamera", Toast.LENGTH_SHORT).show()
        } else {
            chosenLoc = ll
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, zoom))
        }
    }

    // Denne er lagt til i MapsViewModel
    private fun addMarker(ll: LatLng?) {
        if (ll != null) {
            if (::marker.isInitialized) {
                marker.remove()
            }
            marker = mMap.addMarker(MarkerOptions().position(ll).title("markør plassering"))
        }
    }
    // Denne er lagt til i MapsViewModel
    fun getChosenLocation(): LatLng? {
        return chosenLoc
    }
     */

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady: map is ready")
        mMap = googleMap
        mMap.setPadding(0, 430, 0, 240) // padding (left, top, right, bottom)
        mMap.setMinZoomPreference(10f) // jo lavere tall, jo lenger ut fra kartet kan man gå
        mMap.setMaxZoomPreference(20.0f) // hvor langt inn man kan zoome
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.isMyLocationEnabled = true
        if (mLocationPermissionGranted) {
            mapsViewModel.getDeviceLocation(mMap, applicationContext)
        }

        // Create a LatLngBounds that includes the country Norway
        val NORGE = LatLngBounds(
            LatLng(58.019156, 2.141567), LatLng(71.399348, 33.442113)
        )
        // Constrain the camera target to the Norway bounds.
        mMap.setLatLngBoundsForCameraTarget(NORGE)

        if (deviceloc == null) {
            val oslo = LatLng(59.911491, 10.757933)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oslo, DEFAULT_ZOOM))
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        //
        if (event != null) {

            return when (MotionEventCompat.getActionMasked(event)) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d(TAG, "Action was DOWN")
                    previousY = event.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    Log.d(TAG, "Action was UP")
                    // swipe up
                    if (previousY > event.y && previousY-event.y > MIN_DISTANCE) {
                        if (v != null && v.id == R.id.slideUp) {
                            val intent = Intent(this, ShowFireIndex::class.java)
                            startActivity(intent)
                            return true
                        }
                    }
                    return false
                }
                else -> super.onTouchEvent(event)
            }
        }
        return false
    }
}