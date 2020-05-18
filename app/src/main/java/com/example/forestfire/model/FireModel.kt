package com.example.forestfire.model

object FireModel {

    data class FireModel(
        val dager : List<Dag>
    )

    data class Dag (
        val locations: List<Location>
    )

    data class Location(
        val name : String,
        val id : String,
        val danger_index : String //Vær obs på at noen danger_index = '-'
    )
}