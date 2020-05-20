package com.example.forestfire.viewModel.fetchAPI

import androidx.lifecycle.*
import com.example.forestfire.model.FireModel
import com.example.forestfire.repository.FireApiService
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class FireDataViewModel(private val fireService : FireApiService) : ViewModel() {

    val liveFireLocations = MutableLiveData<List<FireModel.Dag>>()

    fun fetchFireLocations(){
        // Returnerer en liste med alle dager -> som igjen inneholder en liste med alle
        // lokasjoner
        viewModelScope.launch {
            val service : List<FireModel.Dag> = fireService.fetchFireData()
            liveFireLocations.postValue(service)
        }
    }

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
            val service : FireApiService = retrofit.create(FireApiService::class.java)
            return modelClass.getConstructor(FireApiService::class.java).newInstance(service)
        }
    }
}
