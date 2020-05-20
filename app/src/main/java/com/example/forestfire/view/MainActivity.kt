package com.example.forestfire.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.forestfire.R
import com.example.forestfire.viewModel.FavoriteViewModel
import com.example.forestfire.viewModel.MapsViewModel
import com.example.forestfire.viewModel.fetchAPI.FireDataViewModel
import com.example.forestfire.viewModel.fetchAPI.LocationForecastViewModel
import com.example.forestfire.viewModel.fetchAPI.StationInfoViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.FileInputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    private var DEFAULT_ZOOM = 15f
    private var MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1
    private var MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1
    private var MIN_DISTANCE = 100
    val CHANNEL_ID = "com.example.forestfire.view.channel1"
    var notificationManager : NotificationManager? = null

    private lateinit var mMap: GoogleMap

    // objekter til fragments
    private lateinit var homeFragment: MapsFragment
    private lateinit var favoriteFragment: FavoritesFragment
    private lateinit var favorites: MutableMap<LatLng, String>
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

    // API viewmodels
    private val fireViewModel : FireDataViewModel by viewModels { FireDataViewModel.InstanceCreator()}
    private val stationInfoViewModel : StationInfoViewModel by viewModels { StationInfoViewModel.InstanceCreator()}
    private val forecastViewModel : LocationForecastViewModel by viewModels { LocationForecastViewModel.InstanceCreator() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // bottom navigation bar
        val nav: BottomNavigationView = findViewById(R.id.menu)

        if (savedInstanceState == null) {
            homeFragment = MapsFragment(stationInfoViewModel, fireViewModel, forecastViewModel)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, homeFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }

        // når vi trykker på noe i menyen blir dette kalt
        nav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.home ->{
                    homeFragment = MapsFragment(stationInfoViewModel, fireViewModel, forecastViewModel)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, homeFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.favorites ->{
                    favoriteFragment = FavoritesFragment(stationInfoViewModel, fireViewModel, forecastViewModel)
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
                    settingsFragment =
                        SettingsFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, settingsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

            }
            true
        }

        // hente favoritter fra internal storage
        try {
            Log.d(TAG, "prøve å hente favoritter fra internal storage")
            val fileInputStream =
                FileInputStream(applicationContext.filesDir.toString() + "/FenceInformation.ser")
            val objectInputStream = ObjectInputStream(fileInputStream)
            favorites = objectInputStream.readObject() as MutableMap<LatLng, String>
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
        // hvis vi ikke fant noe så er listen tom og kan hentes fra
        // favoriteViewModel
        if(!::favorites.isInitialized){
            favorites = favoriteViewModel.favorites
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

    // --------------------------------------- Notification ---------------------------------------
    fun vis_Varsel() {//lage varsligs metode
        val notificationId = 55
        val draTilResultat = Intent(this, this::class.java) // gå til aktivitet etter å trykke på varsling
        val pendingIntent = PendingIntent
            .getActivity(this, 0, draTilResultat, PendingIntent.FLAG_UPDATE_CURRENT)
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


    /*fun onTaskRemoved(){
        // read hashmap to a file
        try {
            Log.d(TAG, "prøve å legge til favoritter i internal storage")

            val fos =
                applicationContext.openFileOutput("YourInfomration.ser", Context.MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)
            oos.writeObject(favorites)
            oos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
     */
}