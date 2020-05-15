package com.example.forestfire.viewModel.fetchAPI

import androidx.lifecycle.*
import com.example.forestfire.model.LocationForecastModel
import com.example.forestfire.repository.LocationForecastService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class LocationForecastViewModel(private val forecastService : LocationForecastService) : ViewModel(){

    val locationForecastLiveData = MutableLiveData<LocationForecastModel.LocationForecastMain>()
    lateinit var forecastInfo : LocationForecastModel.LocationForecastMain
    var favoriteLocationMap = mutableMapOf<LatLng?, ForecastDay>()


    fun fetchLocationForecast(latlon : LatLng?){
        val lat = latlon?.latitude
        val lon = latlon?.longitude
        viewModelScope.launch {
            val locService = forecastService.fetchLocationForecast(lat, lon)
            forecastInfo = locService
            locationForecastLiveData.postValue(locService)
        }
    }

    fun addFavoriteForecast(latlon : LatLng?){
        //Checking if the hashmap does not have the location added
        if(!favoriteLocationMap.containsKey(latlon)){
            fetchLocationForecast(latlon)
            var forecast_list = mutableListOf<Forecast>()
            val temp = forecastInfo.product.time[0].location.temperature.value
            val symbol = forecastInfo.product.time[1].location.symbol.id
            forecast_list.add(Forecast(temp, symbol))
            val forecast = ForecastDay(forecast_list)
            //val forecast1 = Forecast(temp, string)
            favoriteLocationMap[latlon] = forecast
        }
    }

    fun removeFavaoriteForecast(latlon : LatLng?){
        if(favoriteLocationMap.containsKey(latlon)){
            favoriteLocationMap.remove(latlon)
        }
    }

    //ForecastDay består av en liste med forecast for forskjellige dager
    //forecast[0] skal gi dagen i dag, forecast[1] skal gi imrogen
    //og forecast[2] skal gi temperatur og id for to dager frem i tid
    data class ForecastDay(
        val forecast : List<Forecast>
    )

    data class Forecast(
        val temperature : String,
        val symbol_id : String
    )


    class InstanceCreator : ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl("https://in2000-apiproxy.ifi.uio.no/weatherapi/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
            val service : LocationForecastService = retrofit.create(LocationForecastService::class.java)
            return modelClass.getConstructor(LocationForecastService::class.java).newInstance(service)
        }
    }
}