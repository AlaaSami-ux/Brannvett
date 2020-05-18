package com.example.forestfire.model

object StationInfoModel {
    data class GeneralInformation(
        val data : List<StasjonInfo>
    )

    data class StasjonInfo(
        val id : String,  //StasjonsId
        val name : String, //Navn med kapslock
        val shortName : String, //Navn uten kapslock
        val geometry : Geometry //Koordinater
    )

    data class Geometry(
        val coordinates : List<Float>
    )
}