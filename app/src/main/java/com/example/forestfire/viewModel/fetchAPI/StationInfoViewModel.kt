package com.example.forestfire.viewModel.fetchAPI

import android.util.Log
import androidx.lifecycle.*
import com.example.forestfire.model.FireModel
import com.example.forestfire.model.StationInfoModel
import com.example.forestfire.repository.StationService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.system.exitProcess

class StationInfoViewModel(private val stationService : StationService) : ViewModel() {
    private var locCoorMap = mutableMapOf<FireModel.Location, StationInfoModel.Geometry>()
    var stationInfoLiveData = MutableLiveData<List<StationInfoModel.GeneralInformation>>()

    private var favMap = hashMapOf<LatLng?, List<String>>()
    var stationFavDangerLiveData = MutableLiveData<HashMap<LatLng?, List<String>>>()

    var stationThreeDayDanger = MutableLiveData<List<String>>()


    fun fetchData(locList : List<FireModel.Location>){
        // Denne metoden brukes for å fylle hashmappet locCoorMap med alle lokasjonene/stasjonene
        // fra forestFireIndex apiet (FireModel), og deres korresponderende data fra
        // Frost apiet (StationInfoModel). Dette hashmappet brukes videre for å finne
        // nærmeste posisjon til et gitt latlng objekt.
        try{
            if(locCoorMap.isEmpty()){
                Log.d("Inside stationInfoVM", "fetching data")
                viewModelScope.launch {
                    val stationList = mutableListOf<StationInfoModel.GeneralInformation>()
                    for(loc in locList){
                        // Stasjoner som ikke lengre er i bruk har en danger_index på '-'
                        // Disse stasjonene finnes ikke i Frost apiet og vi vil få en 404
                        // error dersom vi kaller på deres id.
                        if(loc.danger_index != "-"){
                            val station = stationService.fetchStationData("SN${loc.id}")
                            stationList.add(station)
                            locCoorMap[loc] = station.data[0].geometry
                            Log.d("Inside stationInfoVM", station.toString())
                        }
                    }
                    stationInfoLiveData.postValue(stationList)
                }
            }
        }catch ( e : java.net.SocketTimeoutException ){
            Log.d("StationVM", e.toString())
        }

    }

    fun fetchThreeDayDanger(posisjon : LatLng, dagListe: List<FireModel.Dag>){
        // Denne metoden produserer en liste av danger_index for tre dager for
        // den gitte posisjonen.
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
        // Denne metoden produserer et hashmap bestpende at latlng objektene vi får inn
        // og deres korresponderende danger_index for tre dager
        // HashMap<LatLng, List<String>>, hvor List<String> er en liste med danger_index
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


    private fun findBestLoc(latlonObjekt : LatLng) : FireModel.Location{
        // Denne metoden finner stasjonen som er nærmest posisjonen som sendes inn
        // og returnerer denne stasjonen (instans av FireModel.Location)

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
                minLatDistance = lat
                minLonDistance = lon
                currentBestLoc = loc
            }
        }
        return currentBestLoc
    }

    class InstanceCreator : ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(40, TimeUnit.SECONDS)
                .build()

            val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl("https://in2000-frostproxy.ifi.uio.no/sources/")
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            val service : StationService = retrofit.create(StationService::class.java)
            return modelClass.getConstructor(StationService::class.java).newInstance(service)
        }
    }
}