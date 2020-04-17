package com.example.forestfire.model

object StationInfoModel {
    data class GeneralInformation(
        val context : String,
        val type : String,
        val apiVersion : String,
        val license : String,
        val createdAt : String,
        val queryTime : Float,
        val currentItemCount : Int,
        val itemsPerPage : Int,
        val offset : Int,
        val totalItemCount : Int,
        val currentLink : String,
        val data : List<StasjonInfo>
    )

    data class StasjonInfo(
        val type : String,
        val id : String,  //StasjonsId
        val name : String, //Navn med kapslock
        val shortName : String, //Navn uten kapslock
        val country : String,
        val countryCode : String,
        val wmoId : Int,
        val geometry : Geometry, //Koordinater
        val masl : Int,
        val validFrom : String,
        val county : String,
        val countyId : Int,
        val municipality : String,
        val municipalityId : Int,
        val stationHolders : List<String>,
        val externalIds : List<String>,
        val wigosId : String
    )

    data class Geometry(
        val type : String,
        val coordinates : List<Float>,
        val nearest : Boolean
    )
}