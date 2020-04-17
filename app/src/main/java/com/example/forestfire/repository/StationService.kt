package com.example.forestfire.repository

import com.example.forestfire.model.StationInfoModel
import retrofit2.http.GET
import retrofit2.http.Query


interface StationService {
    @GET("v0.jsonld") //?ids=SN${loc.id}
    suspend fun fetchStationData(@Query("ids") ids : String) : StationInfoModel.GeneralInformation
}