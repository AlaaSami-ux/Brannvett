package com.example.forestfire.viewModel.fetchAPI

import android.util.Log
import androidx.lifecycle.*
import com.example.forestfire.model.LocationForecastModel
import com.example.forestfire.repository.LocationForecastService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class LocationForecastViewModel(private val forecastService : LocationForecastService) : ViewModel(){

    val locationForecastLiveData = MutableLiveData<LocationForecastModel.LocationForecastMain>()
    lateinit var forecastInfo : LocationForecastModel.LocationForecastMain

    var favMap = hashMapOf<LatLng?, List<FavForecast>>()
    val forecastFavoritesLiveData = MutableLiveData<HashMap<LatLng?, List<FavForecast>>>()
    val threeDayForecast = MutableLiveData<List<FavForecast>>()

    lateinit var c : Calendar

    fun fetchLocationForecast(latlon : LatLng?){
        val lat = latlon?.latitude
        val lon = latlon?.longitude
        viewModelScope.launch {
            val locService = forecastService.fetchLocationForecast(lat, lon)
            forecastInfo = locService
            locationForecastLiveData.postValue(locService)
        }
    }

    fun fetchThreeDayForecast(posisjon : LatLng?){
        c = Calendar.getInstance()
        viewModelScope.launch {
            val forecastList = mutableListOf<FavForecast>()
            val lat = posisjon?.latitude
            val lon = posisjon?.longitude

            val service = forecastService.fetchLocationForecast(lat, lon)
            var temp : String? = null
            var symbol : String? = null

            for(i in 0..2){
                if(i == 0){
                    temp = service.product.time[0].location.temperature.value
                    symbol = service.product.time[1].location.symbol.number
                    forecastList.add(FavForecast(i, temp, symbol))
                } else {
                    val date = getDate(i)
                    for(forecast in service.product.time){
                        if(forecast.to == forecast.from && forecast.to == date){
                            temp = forecast.location.temperature.value
                        }

                        if(forecast.to == date && forecast.from == prevHour(i)){
                            symbol = forecast.location.symbol.number
                        }
                    }

                    if(temp != null && symbol != null){
                        Log.d("LOCATION", "$temp og $symbol")
                        forecastList.add(FavForecast(i, temp, symbol))
                    }
                }
            }
            threeDayForecast.postValue(forecastList)
        }
    }



    fun fetchForecastFavorites(posisjonsListe : List<LatLng?>){
        c = Calendar.getInstance()
        var lat : Double?
        var lon : Double?
        viewModelScope.launch {
            for(pos in posisjonsListe){
                val forecastList = mutableListOf<FavForecast>()
                lat = pos?.latitude
                lon = pos?.longitude

                val service : LocationForecastModel.LocationForecastMain = forecastService.fetchLocationForecast(lat, lon)
                var temp : String? = null
                var symbol : String? = null

                for(i in 0..2) {
                    if (i==0) {
                        temp = service.product.time[0].location.temperature.value
                        symbol = service.product.time[1].location.symbol.number
                        forecastList.add(FavForecast(i, temp, symbol))
                    }else{
                        val date = getDate(i)
                        for(forecast in service.product.time){
                            if(forecast.to == forecast.from && forecast.to == date){
                                temp = forecast.location.temperature.value
                            }

                            if(forecast.to == date && forecast.from == prevHour(i)){
                                symbol = forecast.location.symbol.number
                            }
                        }
                        if (temp != null && symbol != null) {
                            forecastList.add(FavForecast(i, temp, symbol))
                        }
                    }
                }
                favMap[pos] = forecastList
            }
            forecastFavoritesLiveData.postValue(favMap)
        }
    }

    private fun getDate(increase : Int) : String{
        if((c.get(Calendar.MONTH)+1) < 10){
            return c.get(Calendar.YEAR).toString() + "-0" + (c.get(Calendar.MONTH)+1).toString() + "-" +
                    (c.get(Calendar.DAY_OF_MONTH) + increase).toString() + "T12:00:00Z"
        } else {
            return c.get(Calendar.YEAR).toString() + "-" + (c.get(Calendar.MONTH)+1).toString() + "-" +
                    (c.get(Calendar.DAY_OF_MONTH) + increase).toString() + "T12:00:00Z"
        }

    }
    private fun prevHour(increase: Int) : String {
        if((c.get(Calendar.MONTH)+1) < 10){
            return c.get(Calendar.YEAR).toString() + "-0" + (c.get(Calendar.MONTH)+1).toString() + "-" +
                    (c.get(Calendar.DAY_OF_MONTH) + increase).toString() + "T11:00:00Z"
        } else {
            return c.get(Calendar.YEAR).toString() + "-" + (c.get(Calendar.MONTH)+1).toString() + "-" +
                    (c.get(Calendar.DAY_OF_MONTH) + increase).toString() + "T11:00:00Z"
        }

    }

    data class FavForecast(
        val dag : Int,
        val temperature : String?,
        val symbol_id : String?
    )


    class InstanceCreator : ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(40, TimeUnit.SECONDS)
                .build()

            val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl("https://in2000-apiproxy.ifi.uio.no/weatherapi/")
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
            val service : LocationForecastService = retrofit.create(LocationForecastService::class.java)
            return modelClass.getConstructor(LocationForecastService::class.java).newInstance(service)
        }
    }
}