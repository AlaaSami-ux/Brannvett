package com.example.forestfire.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.forestfire.R
import com.example.forestfire.viewModel.FavoriteViewModel
import com.example.forestfire.viewModel.MapsViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
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

    // the location of the device
    private var deviceloc: LatLng? = null
    // Autocomplete fragment
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    //private var previousX: Float = 0F
    private var previousY: Float = 0F // used for checking if there has been a swipe upward

    private lateinit var favoriteBtn: ImageButton // button for adding as favorite
    private lateinit var slideUp: CardView // the cardview that opens a new activity upon swipe up
    private var mLocationPermissionGranted = false // assume location permission is not granted

    // the ViewModel for the map
    val mapsViewModel: MapsViewModel by viewModels()
    val favoriteViewModel: FavoriteViewModel by viewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // initialize the cardView that slides up/opens a new activity
        slideUp = findViewById(R.id.slideUp)
        // set on touch listener for only this view
        slideUp.setOnTouchListener(this)
        favoriteBtn = findViewById(R.id.favoriteBtnOnMap)
        favoriteBtn.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "favorite button clicked")
            favoriteViewModel.buttonClick(favoriteBtn)
        })

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

    private fun buttonClick(){

    }

    private fun getLocationPermission() {
        // get permission to get current location
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
        val norge = LatLngBounds(
            LatLng(58.019156, 2.141567), LatLng(71.399348, 33.442113)
        )
        // Constrain the camera target to the Norway bounds.
        mMap.setLatLngBoundsForCameraTarget(norge)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        // Jeg tror dette egentlig skal være i viewmodel men jeg vet ikke hvordan
        // må ha performclick for de med nedsatt syn
        if (event != null) {
            return when (event.action) {
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