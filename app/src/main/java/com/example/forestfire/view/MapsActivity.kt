package com.example.forestfire.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.example.forestfire.R
import com.example.forestfire.viewModel.FavoriteViewModel
import com.example.forestfire.viewModel.MapsViewModel
import com.example.forestfire.viewModel.Varsling
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_maps.*


val CHANNEL_ID = "com.example.forestfire.view.channel1"
var notificationManager : NotificationManager? = null

class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback,
    View.OnTouchListener {

    val TAG = "MapsActivity"
    private var DEFAULT_ZOOM = 15f
    private var MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1
    private var MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1
    private var MIN_DISTANCE = 100

    private lateinit var mMap: GoogleMap

    // objekter til fragments
    lateinit var homeFragment: MapFragment
    lateinit var favoriteFragment: FavoritesFragment
    lateinit var infoFragment: InfoFragment
    lateinit var settingsFragment: SettingsFragment

    // the location of the device
    private var deviceloc: LatLng? = null

    // Autocomplete fragment
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    //private var previousX: Float = 0F
    private var previousY: Float = 0F // used for checking if there has been a swipe upward

    private lateinit var valgtSted: TextView
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

        // bottom navigation bar
        val nav: BottomNavigationView = findViewById(R.id.menu)
        // når vi trykker på noe i menyen blir dette kalt
        nav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.home ->{
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                }
                R.id.favorites ->{
                    favoriteFragment = FavoritesFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.mapslayout, favoriteFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

                R.id.info ->{
                    infoFragment = InfoFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.mapslayout, infoFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.settings ->{
                    settingsFragment = SettingsFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.mapslayout, settingsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

            }
            true
        }

        valgtSted = findViewById(R.id.valgtSted)
        // initialize the cardView that slides up/opens a new activity
        slideUp = findViewById(R.id.slideUp)
        // set on touch listener for only this view
        slideUp.setOnTouchListener(this)
        favoriteBtn = findViewById(R.id.favoriteBtn)
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
                valgtSted.text=place.name
            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
                Toast.makeText(applicationContext, "Det har skjedd en feil", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        getLocationPermission()
        /*
        goTilInfoActivity()
        goTilFavorittActivity()
        goTilSettingActivity()

         */
        // --------------------------------- Varsling ---------------------------------------------
      //  var varsling : Varsling = Varsling(this, CHANNEL_ID)
        //------hentilg varsling systemet-----
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // -----henting av varsling kanal -----
        createNotificationChannel(CHANNEL_ID, "Skogbrann", "NB: BRANNFARE")

            //Toast.makeText(applicationContext, "INFO", Toast.LENGTH_SHORT)
            //   .show()
            vis_Varsel()
        //----------------------------------------------------------------------------------------
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
            valgtSted.text="Din posisjon"
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
                    if (previousY > event.y && previousY - event.y > MIN_DISTANCE) {
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

    // --------------------------------------- Notification ---------------------------------------
    fun vis_Varsel() {//lage varsligs metode
        val notificationId: Int = 55
        val draTilResutat = Intent(this, InfoActivity::class.java) // gå til aktivitet etter å trykke på varsling
        val pendingIntent = PendingIntent
            .getActivity(this, 0, draTilResutat, PendingIntent.FLAG_UPDATE_CURRENT)
            .apply { Intent.FLAG_ACTIVITY_CLEAR_TASK }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Fare")
            .setContentText("Skogbrannfare på ditt favoritt sted")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager?.notify(notificationId, notification)
    }

    fun createNotificationChannel(id: String, name: String, channelDiscription: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel: NotificationChannel =
                NotificationChannel(id, name, importance).apply { description = channelDiscription }
            notificationManager?.createNotificationChannel(channel)
        }
    }
    /*
    //---------------------------------------------------------------------------------------------

    fun goTilInfoActivity(){
        info2.setOnClickListener{
           val intent =  Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }
    }
    fun goTilFavorittActivity() {
        favoritt.setOnClickListener {
            val intent = Intent(this, FavorittActivity::class.java)
            startActivity(intent)
        }
    }
    fun goTilSettingActivity() {
        setting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }
     */
}