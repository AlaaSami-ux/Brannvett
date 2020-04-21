package com.example.forestfire.viewModel


import android.widget.ImageButton
import androidx.lifecycle.ViewModel
import com.example.forestfire.R
import com.google.android.gms.maps.model.LatLng

class FavoriteViewModel : ViewModel(){
    fun buttonClick(btn: ImageButton){
        btn.setBackgroundResource(R.drawable.ic_favorite_black_24dp)
    }

    fun addedFavorite(latLng: LatLng?){
        // Her vil jeg legge inn LatLng som favoritt i en slags liste eller noe
    }
}