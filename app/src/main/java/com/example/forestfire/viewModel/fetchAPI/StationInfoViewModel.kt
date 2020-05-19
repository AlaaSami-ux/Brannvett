package com.example.forestfire.viewModel.fetchAPI

import android.util.Log
import androidx.lifecycle.*
import com.example.forestfire.model.FireModel
import com.example.forestfire.model.StationInfoModel
import com.example.forestfire.repository.StationService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.math.abs
import kotlin.system.exitProcess

class StationInfoViewModel(private val stationService : StationService) : ViewModel() {
    var locCoorMap = mutableMapOf<FireModel.Location, StationInfoModel.Geometry>()
    var stationInfoLiveData = MutableLiveData<List<StationInfoModel.GeneralInformation>>()

    var favMap = hashMapOf<LatLng?, List<String>>()
    var stationFavDangerLiveData = MutableLiveData<HashMap<LatLng?, List<String>>>()

    var stationThreeDayDanger = MutableLiveData<List<String>>()


    fun fetchData(locList : List<FireModel.Location>){
        if(locCoorMap.isEmpty()){
            Log.d("Inside stationInfoVM", "fetching data")
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
    }

    fun fetchThreeDayDanger(posisjon : LatLng, dagListe: List<FireModel.Dag>){
        viewModelScope.launch {
            val bestLoc = findBestLoc(posisjon)
            val dangerList = mutableListOf<String>()
            for(dag in dagListe){
                for(loc in dag.locations){
                    if(loc.id == bestLoc.id){
                        dangerList.add(loc.danger_index)
                    }
                }
            }
            stationThreeDayDanger.postValue(dangerList)
        }
    }

    fun fetchFavDanger(posisjonsListe : List<LatLng>, dagListe : List<FireModel.Dag>){

        viewModelScope.launch {
            for(pos in posisjonsListe){

                val bestLoc = findBestLoc(pos)
                Log.d("Fav danger StationVM", "bestLoc = " + bestLoc.name + " pos: " + pos.toString())
                val dangerList = mutableListOf<String>()
                for(dag in dagListe){
                    for(loc in dag.locations){
                        if(loc.id == bestLoc.id){
                            dangerList.add(loc.danger_index)
                        }
                    }
                }
                favMap[pos] = dangerList
            }
            stationFavDangerLiveData.postValue(favMap)
        }
    }

    data class DangerIndex(
        val danger_index : String
    )

    private fun findBestLoc(latlonObjekt : LatLng) : FireModel.Location{
        if(locCoorMap.isEmpty()){
            Log.d("StationInfoViewModel", "locCoorMap is empty")
            exitProcess(1)  // Dersom hashmappet ikke er fylt stopper vi prosessen
        }



        val userLng = latlonObjekt.longitude
        val userLat = latlonObjekt.latitude

        var currentBestLoc : FireModel.Location = locCoorMap.keys.elementAt(0)
        var minLatDistance = 1000F
        var minLonDistance = 1000F

        var lat : Float
        var lon : Float

        for((loc,geo) in locCoorMap){
            lat = abs(geo.coordinates[1] - userLat.toFloat())
            lon = abs(geo.coordinates[0] - userLng.toFloat())

            if(minLatDistance > lat && minLonDistance > lon){
                Log.d("BestLoc method", "geo coor ("+geo.coordinates[1].toString() + ", " + geo.coordinates[0].toString() + ")")
                minLatDistance = lat
                minLonDistance = lon
                currentBestLoc = loc
            }
        }
        return currentBestLoc
    }

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