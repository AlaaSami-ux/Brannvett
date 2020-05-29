package com.example.forestfire.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.example.forestfire.R
import com.example.forestfire.viewModel.fetchAPI.FireDataViewModel
import com.example.forestfire.viewModel.fetchAPI.LocationForecastViewModel
import com.example.forestfire.viewModel.fetchAPI.StationInfoViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    // objekter til fragments
    private lateinit var homeFragment: MapsFragment
    private lateinit var favoriteFragment: FavoritesFragment
    private lateinit var infoFragment: InfoFragment
    private lateinit var settingsFragment: SettingsFragment

    fun isOnline(): Boolean{
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
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
        showNoConnectionDialog()
        return false
    }

    fun showNoConnectionDialog(){
        Log.d(TAG, "viser prøv igjen dialog")
        val builder = androidx.appcompat.app.AlertDialog.Builder(this, R.style.CustomAlertDialog)
        builder.setMessage(getString(R.string.ingenInternett))
        builder.setPositiveButton(getString(R.string.lukkApp)) { _, _ ->
            ActivityCompat.finishAffinity(this)
        }
        val dialog =builder.create()
        dialog.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // bottom navigation bar
        val nav: BottomNavigationView = findViewById(R.id.menu)

        // API viewmodels
        val fireViewModel: FireDataViewModel by viewModels { FireDataViewModel.InstanceCreator() }
        val stationInfoViewModel: StationInfoViewModel by viewModels { StationInfoViewModel.InstanceCreator() }
        val forecastViewModel: LocationForecastViewModel by viewModels { LocationForecastViewModel.InstanceCreator() }


        val pos = LatLng(59.9139, 10.7522)
        forecastViewModel.fetchLocationForecast(pos)
        forecastViewModel.locationForecastLiveData.observe(this, Observer {
            //Tester om temperaturen stemmer
            val equal = testData(13.0, it.product.time[0].location.temperature.value)
            if(equal == 0){
                Log.d("MainActivityTest", "Expected and recieved value is the same")
            }
        })


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
    }


    private fun<T> testData(expected : T, got : T) : Int{
        //Returnerer 0 dersom objektene er like
        return expected.toString().compareTo(got.toString())
    }
}