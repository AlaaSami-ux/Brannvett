package com.example.forestfire.viewModel.fetchAPI

import androidx.lifecycle.*
import com.example.forestfire.model.FireModel
import com.example.forestfire.repository.FireApiService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class FireDataViewModel(private val fireService : FireApiService) : ViewModel() {


    val liveAllFireData = MutableLiveData<List<FireModel.Dag>>()
    val liveFireStation = MutableLiveData<FireModel.Location>()
    val liveFireLocations = MutableLiveData<List<FireModel.Location>>()


    fun fetchAllFireData(context : LifecycleOwner, dag : Int, stasjonsID : String) {
        //Returnerer en list med alle dagene
        viewModelScope.launch {
            val fireApi = fireService.fetchFireData()
            liveAllFireData.postValue(fireApi)
        }
    }

    fun fetchFireStation(context: LifecycleOwner, dag: Int, stasjonsID: String){
        //returnerer en spesifikk lokasjon basert på oppgitt stasjons id
        viewModelScope.launch {
            val service : List<FireModel.Dag> = fireService.fetchFireData()
            val locs : List<FireModel.Location> = service[dag].locations
            for(loc in locs){
                if(loc.id == stasjonsID){
                    liveFireStation.postValue(loc)
                    //TODO gi passende feilmelding til bruker dersom liveFireStation ikke blir oppdatert
                }
            }
        }

    }


    fun fetchFireLocations(context : LifecycleOwner, dag : Int){
        //Returnerer en liste med alle lokasjoner for en gitt dag
        viewModelScope.launch {
            val service : List<FireModel.Dag> = fireService.fetchFireData()
            liveFireLocations.postValue(service[dag].locations)
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

