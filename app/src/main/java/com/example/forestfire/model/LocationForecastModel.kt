package com.example.forestfire.model


object LocationForecastModel{
    data class LocationForecastMain(
        val product : Product
    )

    data class Product(
        val time : List<Forecast>
    )

    data class Forecast(
        val to : String,
        val from : String,
        val location : LocationForecast
    )


    /*
    * The ForecastLocation object has three different appearances
    * 1. This will give you the temperature for the given hour of a given day
    * 2. This will give you the symbol name and id for a given time period.
    * 3. This will give you the min/max temperatures for a 6 hour time period
     */
    data class LocationForecast(
        //1.
        val windDirection : WindDirection,
        val windSpeed : WindSpeed,
        val temperature : Temperature,

        //2. Provides the symbols
        val symbol : Symbol
    )


    data class  Symbol(
        val id : String,    //Tekstlig beskrivelse av symbolet. F.eks "LightCloud"
        val number : String   //Tallet som representerer symbolet
    )

    data class WindDirection(
        val name : String,
        val deg : String,
        val id : String
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


}