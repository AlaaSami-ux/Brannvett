package com.example.forestfire.repository

import com.example.forestfire.model.FireModel
import retrofit2.http.GET

interface FireApiService {
    @GET("forestfireindex/1.1/.json")
    suspend fun fetchFireData() : List<FireModel.Dag>
}