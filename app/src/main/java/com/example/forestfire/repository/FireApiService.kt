package com.example.forestfire.repository

import com.example.forestfire.model.FireModel
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface FireApiService {
    @GET("forestfireindex/1.1/.json")
    suspend fun fetchFireData() : List<FireModel.Dag>
}