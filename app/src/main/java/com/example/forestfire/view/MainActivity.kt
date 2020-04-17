package com.example.forestfire.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.forestfire.R
import com.example.forestfire.model.FireModel
import com.example.forestfire.viewModel.fetchAPI.FireDataViewModel
import com.example.forestfire.viewModel.fetchAPI.StationInfoViewModel

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
                val station = stationInfoViewModel.findBestLocation(8F, 62F)
                drawData(station)
            })
        })
    }


    private fun drawData(loc : FireModel.Location){
        findViewById<TextView>(R.id.name_text).text = loc.name
    }
}