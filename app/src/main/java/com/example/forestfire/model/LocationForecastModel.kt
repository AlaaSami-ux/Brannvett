package com.example.forestfire.model

import com.google.gson.annotations.SerializedName

object LocationForecastModel{
    data class LocationForecastMain(
        val meta : Meta,
        val product : Product,
        val created : String
    )

    data class Meta(
        val model : Model
    )

    data class Model(
        val to : String,
        val name : String,
        val nextrun : String,
        val from : String,
        val runended : String,
        val termin : String
    )

    data class Product(
        val time : List<Forecast>,
        @SerializedName("class") val klasse : String
    )

    data class Forecast(
        val to : String,
        val datatype : String,
        val from : String,
        val location : ForecastLocation
    )


    /*
    * The ForecastLocation object has three different appearances
    * 1. This will give you the temperature for the given hour of a given day
    * 2. This will give you the symbol name and id for a given time period.
    * 3. This will give you the min/max temperatures for a 6 hour time period
     */
    data class ForecastLocation(
        //1.
        val pressure : Pressure,
        val windDirection : WindDirection,
        val highClouds : HighClouds,
        val mediumClouds : MediumClouds,
        val lowClouds : LowClouds,
        val temperatureProbability : TempProb,
        val dewpointTemperature : DewpointTemp,
        val cloudiness : Cloudiness,
        val fog : Fog,
        val humidity : Humidity,
        val windSpeed : WindSpeed,
        val temperature : Temperature,
        val windGust : WindGust,
        val windProbability : WindProbability,

        //2.
        val precipitation : Precipitation, //This also appears in 3
        val symbol : Symbol,

        //3.
        val minTemperature : Temperature,
        val maxTemperature : Temperature,

        //Common
        val latitude : String,
        val longitude : String,
        val altitude : String
    )

    data class  Precipitation(
        val unit : String,
        val value : String,
        val maxvalue : String,
        val minvalue : String
    )

    data class  Symbol(
        @SerializedName("id") val name : String,    //Tekstlig beskrivelse av symbolet. F.eks "LightCloud"
        @SerializedName("number") val id : String   //Tallet som representerer symbolet
    )

    data class Pressure(
        val value : String,
        val unit : String,
        val id : String
    )

    data class WindDirection(
        val name : String,
        val deg : String,
        val id : String
    )

    data class HighClouds(
        val id : String,
        val percent : String
    )

    data class MediumClouds(
        val id : String,
        val percent : String
    )

    data class LowClouds(
        val percent : String,
        val id : String
    )

    data class TempProb(
        val unit : String,
        val value : String
    )

    data class DewpointTemp(
        val value : String,
        val unit : String,
        val id : String
    )

    data class Cloudiness(
        val id : String,
        val percent : String
    )

    data class Fog(
        val id : String,
        val percent : String
    )

    data class Humidity(
        val value : String,
        val unit : String
    )

    data class WindSpeed(
        val mps : String,
        val name : String,
        val beaufort : String,
        val id : String
    )

    data class Temperature(
        val id : String,
        val unit : String,
        val value : String
    )

    data class  WindGust(
        val mps : String,
        val id : String
    )

    data class WindProbability(
        val unit : String,
        val value : String
    )

}