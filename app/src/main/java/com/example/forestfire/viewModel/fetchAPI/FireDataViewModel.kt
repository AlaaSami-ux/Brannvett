package com.example.forestfire.viewModel.fetchAPI

import androidx.lifecycle.*
import com.example.forestfire.model.FireModel
import com.example.forestfire.repository.FireApiService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class FireDataViewModel(private val fireService : FireApiService) : ViewModel() {


    val liveFireStation = MutableLiveData<FireModel.Location>()
    val liveFireLocations = MutableLiveData<List<FireModel.Dag>>()


    val favMap = hashMapOf<LatLng?, List<DangerIndex>>()
    val favFireLiveData = MutableLiveData<HashMap<LatLng?, List<DangerIndex>>>()



    fun fetchFireStation(context: LifecycleOwner, dag: Int, stasjonsID: String){
        //returnerer en spesifikk lokasjon basert på oppgitt stasjons id
        viewModelScope.launch {
            val service : List<FireModel.Dag> = fireService.fetchFireData()
            val locs : List<FireModel.Location> = service[dag].locations
            var inne = 0
            for(loc in locs){
                if(loc.id == stasjonsID){
                    inne  = 1
                    liveFireStation.postValue(loc)
                }
            }
            if(inne == 0){
                //TODO gi passende feilmelding til programmerer dersom liveFireStation ikke blir oppdatert
            }
        }

    }

    fun fetchFavoritesDangerIndex(posisjonsliste : List<LatLng?>, stasjonsId : String){
       viewModelScope.launch {
           val service : List<FireModel.Dag>  = fireService.fetchFireData()
           for(pos in posisjonsliste){
               val dangerList = mutableListOf<DangerIndex>()
               for(i in 0..2){
                   var danger : String? = null
                   for(loc in service[i].locations){
                       if("SN${loc.id}" == stasjonsId){
                           danger = loc.danger_index
                       }
                   }
                   dangerList.add(DangerIndex(danger))
               }
               favMap[pos] = dangerList
           }
           favFireLiveData.postValue(favMap)
       }
    }


    data class DangerIndex(
        val danger_index : String?
    )

    fun fetchFireLocations(){
        //Returnerer en liste med alle lokasjoner for en gitt dag
        viewModelScope.launch {
            val service : List<FireModel.Dag> = fireService.fetchFireData()
            liveFireLocations.postValue(service)
        }
    }


    class InstanceCreator : ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl("https://in2000-apiproxy.ifi.uio.no/weatherapi/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
            val service : FireApiService = retrofit.create(FireApiService::class.java)
            return modelClass.getConstructor(FireApiService::class.java).newInstance(service)
        }
    }
}

/*
//Disse metodene må gjøres i et view.

private val viewModel : FireDataViewModel by viewModels {FireDataViewModel.InstanceCreator()}

//Eksempel på hvordan man gjør kall på fetchFireData

viewModel.fetchAllFireData(this, 0, "36560")
viewModel.liveAllFireData.observe(this, Observer {dagList : List<FireModel.Dag> ->
    val locationList : List<FireModel.Location> = dagList[dag].locations
    for(loc in locationList){
        if(loc.id == stasjonsID){
            drawData(loc)
            break
        }
    }
})


//Eksempel kall på fetchFireStation
viewModel.fetchFireStation(this, dag, stasjonsID)
viewModel.liveFireStation.observe(this, Observer {location ->
    drawData(location)
})


//Eksempel kall på fetchFireLocations
viewModel.fetchFireLocations(this, dag)
viewModel.liveFireLocations.observe(this, Observer{locationList ->
    drawData(locationList[24])
})



//Eksempel på hvordan man han hente ut navn på en gitt lokasjon
private fun drawData(loc : FireModel.Location){
    findViewById<TextView>(R.id.name_text).text = loc.name
}

*/

