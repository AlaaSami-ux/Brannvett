package com.example.forestfire.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
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
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


val CHANNEL_ID = "com.example.forestfire.view.channel1"
var notificationManager : NotificationManager? = null

class MapsActivity : AppCompatActivity(){

    val TAG = "MapsActivity"
    private var DEFAULT_ZOOM = 15f
    private var MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1
    private var MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1
    private var MIN_DISTANCE = 100

    private lateinit var mMap: GoogleMap

    // objekter til fragments
    lateinit var homeFragment: MapsFragment
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

        homeFragment = MapsFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        // når vi trykker på noe i menyen blir dette kalt
        nav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.home ->{
                    homeFragment = MapsFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, homeFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.favorites ->{
                    favoriteFragment = FavoritesFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, favoriteFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

                R.id.info ->{
                    infoFragment = InfoFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, infoFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.settings ->{
                    settingsFragment = SettingsFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, settingsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

            }
            true
        }

        /*
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
        */
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

    // --------------------------------------- Notification ---------------------------------------
    fun vis_Varsel() {//lage varsligs metode
        val notificationId: Int = 55
        val draTilResutat = Intent(this, this::class.java) // gå til aktivitet etter å trykke på varsling
        val pendingIntent = PendingIntent
            .getActivity(this, 0, draTilResutat, PendingIntent.FLAG_UPDATE_CURRENT)
            .apply { Intent.FLAG_ACTIVITY_CLEAR_TASK }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Fare")
            .setContentText("Skogbrannfare på ditt favoritt sted")
            .setSmallIcon(R.drawable.ic_hot_blond_lady_24dp)
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
}