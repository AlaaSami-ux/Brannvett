package com.example.forestfire.view

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.FragmentTransaction
import com.example.forestfire.R
import com.example.forestfire.viewModel.fetchAPI.FireDataViewModel
import com.example.forestfire.viewModel.fetchAPI.LocationForecastViewModel
import com.example.forestfire.viewModel.fetchAPI.StationInfoViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val CHANNEL_ID = "com.example.forestfire.view.channel1"
    private var notificationManager: NotificationManager? = null

    // objekter til fragments
    private lateinit var homeFragment: MapsFragment
    private lateinit var favoriteFragment: FavoritesFragment
    lateinit var infoFragment: InfoFragment
    lateinit var settingsFragment: SettingsFragment

    /*fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
        }
        return false
    }

     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        if (!isOnline(this)){
        Log.d(TAG, "ingen internettilgang")
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.ingenInternett))
            builder.setPositiveButton(getString(R.string.OK)) { _, _ ->
                ActivityCompat.finishAffinity(this)
            }
            val dialog =builder.create()
            dialog.show()
        }
         */

        // Getting Google Play availability status

        // Getting Google Play availability status
        val status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(baseContext)

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.ingenInternett))
            builder.setPositiveButton(getString(R.string.OK)) { _, _ ->
                ActivityCompat.finishAffinity(this)
            }
            val dialog =builder.create()
            dialog.show()
        }

        /*
        // ICMP
        fun isOnline(): Boolean {
            val runtime:Runtime = Runtime.getRuntime();
            try {
                val ipProcess: Process = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
                val exitValue: Int = ipProcess.waitFor();
                return (exitValue == 0);
            }
            catch (e: IOException)          { e.printStackTrace(); }
            catch (e: InterruptedException) { e.printStackTrace(); }

            return false;
        }
        if (isOnline()){

        }
         */

        // bottom navigation bar
        val nav: BottomNavigationView = findViewById(R.id.menu)

        // API viewmodels
        val fireViewModel: FireDataViewModel by viewModels { FireDataViewModel.InstanceCreator() }
        val stationInfoViewModel: StationInfoViewModel by viewModels { StationInfoViewModel.InstanceCreator() }
        val forecastViewModel: LocationForecastViewModel by viewModels { LocationForecastViewModel.InstanceCreator() }

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
            when (item.itemId) {
                R.id.home -> {
                    homeFragment =
                        MapsFragment(stationInfoViewModel, fireViewModel, forecastViewModel)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, homeFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.favorites -> {
                    favoriteFragment =
                        FavoritesFragment(stationInfoViewModel, fireViewModel, forecastViewModel)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, favoriteFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

                R.id.info -> {
                    infoFragment = InfoFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, infoFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.settings -> {
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


        /*// hente favoritter fra internal storage
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
        }*/


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
        val draTilResultat =
            Intent(this, this::class.java) // gå til aktivitet etter å trykke på varsling
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