package com.example.forestfire.viewModel

import androidx.lifecycle.ViewModel
import kotlin.math.roundToInt

class UnitSystemViewModel : ViewModel() {

    fun toFahrenheit(temp : Double) : Int{
        return (temp * 9/5.0 + 32).roundToInt()
    }

    fun toMph(speed : Double): Double {
        return ((speed * 2.2369) * 10.0).roundToInt() / 10.0
    }
}