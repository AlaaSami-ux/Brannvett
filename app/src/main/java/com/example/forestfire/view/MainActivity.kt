package com.example.forestfire.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.example.forestfire.R
import com.example.forestfire.model.FireModel
import com.example.forestfire.viewModel.Varsling
import com.example.forestfire.viewModel.fetchAPI.FireDataViewModel
import com.example.forestfire.viewModel.fetchAPI.StationInfoViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_maps.*

class MainActivity : AppCompatActivity() {

    private val fireViewModel : FireDataViewModel by viewModels { FireDataViewModel.InstanceCreator()}
    private val stationInfoViewModel : StationInfoViewModel by viewModels { StationInfoViewModel.InstanceCreator()}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val dag = 0

        fireViewModel.fetchFireLocations(this, dag)
        fireViewModel.liveFireLocations.observe(this, Observer { locList ->
            stationInfoViewModel.fetchData(locList)
            stationInfoViewModel.stationInfoLiveData.observe(this, Observer {
                val station = stationInfoViewModel.findBestLocation(LatLng(8.0, 62.0))
                drawData(station)
            })
        })
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)

    }



    private fun drawData(loc : FireModel.Location){
        findViewById<TextView>(R.id.name_text).text = loc.name
    }
}