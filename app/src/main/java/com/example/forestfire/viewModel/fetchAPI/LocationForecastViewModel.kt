package com.example.forestfire.viewModel.fetchAPI

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.forestfire.model.LocationForecastModel
import com.example.forestfire.repository.LocationForecastService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class LocationForecastViewModel(private val forecastService : LocationForecastService) : ViewModel(){

    val locationForecastLiveData = MutableLiveData<LocationForecastModel.LocationForecastMain>()

    fun fetchLocationForecast(lat : Float, lon : Float){
        viewModelScope.launch {
            val locService = forecastService.fetchLocationForecast(lat, lon)
            locationForecastLiveData.postValue(locService)
        }
    }

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