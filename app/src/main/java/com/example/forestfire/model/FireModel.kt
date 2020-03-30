package com.example.forestfire.model

object FireModel {
    data class Dag (
        val locations: List<Location>,
        val time : Tid
    )

    data class Location(
        val name : String,
        val county : String,
        val id : String,
        val danger_index : String //Vær obs på at noen danger_index = '-'
    )

    data class Tid(
        val from : String,
        val to : String
    )
}