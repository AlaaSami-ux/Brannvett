package com.example.forestfire.viewModel


import android.util.Log
import android.widget.ImageButton
import androidx.lifecycle.ViewModel
import com.example.forestfire.R
import com.google.android.gms.maps.model.LatLng

class FavoriteViewModel : ViewModel(){

    val TAG = "FavoriteViewModel"
    var favorite = false

    fun buttonClick(btn: ImageButton){
        Log.d(TAG, "favorite button clicked")
        if (!favorite){
            btn.setBackgroundResource(R.drawable.ic_favorite_black_24dp)
        } else {
            btn.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp)
        }
    }

    fun changeFavoriteBoolean(){
        favorite = !favorite
    }

    fun addedFavorite(latLng: LatLng?){
        // Her vil jeg legge inn LatLng som favoritt i en slags liste eller noe
    }
}