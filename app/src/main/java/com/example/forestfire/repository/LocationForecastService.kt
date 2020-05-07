package com.example.forestfire.repository

import com.example.forestfire.model.LocationForecastModel
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationForecastService {
    @GET("locationforecast/1.9/.json")
    suspend fun fetchLocationForecast(
        @Query("lat") lat : Float,
        @Query("lon") lon : Float) : LocationForecastModel.LocationForecastMain

}