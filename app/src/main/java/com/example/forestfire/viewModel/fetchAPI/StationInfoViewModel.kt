package com.example.forestfire.viewModel.fetchAPI

import android.util.Log
import androidx.lifecycle.*
import com.example.forestfire.model.FireModel
import com.example.forestfire.model.StationInfoModel
import com.example.forestfire.repository.StationService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.math.abs
import kotlin.system.exitProcess

class StationInfoViewModel(private val stationService : StationService) : ViewModel() {
    /*
    Hent Alle stasjoner gitt en dag fra FireDataViewModel
    Plasser i Hashmap med sine koordinater fra StationInfoModel
    */

    var locCoorMap = mutableMapOf<FireModel.Location, StationInfoModel.Geometry>()
    var stationInfoLiveData = MutableLiveData<List<StationInfoModel.GeneralInformation>>()

    fun fetchData(locList : List<FireModel.Location>){
        viewModelScope.launch {
            val stationList = mutableListOf<StationInfoModel.GeneralInformation>()
            for(loc in locList){
                if(loc.danger_index != "-"){
                    val station = stationService.fetchStationData("SN${loc.id}")
                    stationList.add(station)
                    locCoorMap[loc] = station.data[0].geometry
                }
            }
            stationInfoLiveData.postValue(stationList)
        }
    }

    fun findBestLocation(latlonObjekt : LatLng) : FireModel.Location{
        // Sjekker om hashmappet med alle lokasjoner og koordinater er fylt
        if(locCoorMap.isEmpty()){
            Log.d("StationInfoViewModel", "locCoorMap is empty")
            exitProcess(1)  // Dersom hashmappet ikke er fylt stopper vi prosessen
        }

        userLon = latlonObjekt.getLon()
        userLat = latlonObjekt.getLat()

        var currentBestLoc : FireModel.Location = locCoorMap.keys.elementAt(0)
        var currentLength = 1000F

        var lon : Float
        var lat : Float
        var lonLat : Float

        for((loc,geo) in locCoorMap){
            lon = abs(geo.coordinates[0] - userLon)
            lat = abs(geo.coordinates[1] - userLat)

            lonLat = abs(lon - lat)

            if(lonLat < currentLength){
                currentLength = lonLat
                currentBestLoc = loc
            }
        }
        return currentBestLoc
    }

   /* fun findBestLocation(userLon : Float, userLat : Float) : FireModel.Location{
        // Sjekker om hashmappet med alle lokasjoner og koordinater er fylt
        if(locCoorMap.isEmpty()){
            Log.d("StationInfoViewModel", "locCoorMap is empty")
            exitProcess(1)  // Dersom hashmappet ikke er fylt stopper vi prosessen
        }

        var currentBestLoc : FireModel.Location = locCoorMap.keys.elementAt(0)
        var currentLength = 1000F

        var lon : Float
        var lat : Float
        var lonLat : Float

        for((loc,geo) in locCoorMap){
            lon = abs(geo.coordinates[0] - userLon)
            lat = abs(geo.coordinates[1] - userLat)

            lonLat = abs(lon - lat)

            if(lonLat < currentLength){
                currentLength = lonLat
                currentBestLoc = loc
            }
        }
        return currentBestLoc
    } */


    class InstanceCreator : ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl("https://in2000-frostproxy.ifi.uio.no/sources/")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            val service : StationService = retrofit.create(StationService::class.java)
            return modelClass.getConstructor(StationService::class.java).newInstance(service)
        }
    }
}