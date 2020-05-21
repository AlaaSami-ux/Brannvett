package com.example.forestfire.viewModel


import android.util.Log
import android.widget.ImageButton
import androidx.lifecycle.ViewModel
import com.example.forestfire.R
import com.google.android.gms.maps.model.LatLng

class FavoriteViewModel : ViewModel(){

    val TAG = "FavoriteViewModel"
    var btnclicked = false

    var favoriteList: MutableList<LatLng> = ArrayList()
    var favorites: MutableMap<LatLng, String> = mutableMapOf()

    fun buttonClick(btn1: ImageButton, btn2: ImageButton){
        Log.d(TAG, "favorite button clicked")
        if (!btnclicked){
            btn1.setBackgroundResource(R.drawable.ic_favorite_black_24dp)
            btn2.setBackgroundResource(R.drawable.ic_favorite_black_24dp)
        } else {
            btn1.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp)
            btn2.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp)
        }
    }

    fun setBtnUnClicked(btn1: ImageButton, btn2: ImageButton){
        btn1.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp)
        btn2.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp)
        btnclicked = false
    }

    fun setBtnClicked(btn1: ImageButton, btn2: ImageButton){
        btn1.setBackgroundResource(R.drawable.ic_favorite_black_24dp)
        btn2.setBackgroundResource(R.drawable.ic_favorite_black_24dp)
        btnclicked = true
    }

    fun changeFavoriteBoolean(){
        btnclicked = !btnclicked
    }

    fun isBtnClicked() : Boolean{
        return btnclicked
    }

    fun isFavorite(latlng: LatLng, place: String) : Boolean{
        if (favorites.containsKey(latlng) || favorites.containsValue(place)){
            return true
        }
        return false
    }

    fun addFavorite(latlng: LatLng, place: String){
        // Her vil jeg legge inn LatLng som favoritt i en slags liste eller noe
        if(!favorites.containsKey(latlng)){
            favorites.put(latlng, place)
            btnclicked = true
            Log.d(TAG, "added favorite. Size of favorites list: " + favorites.count())
        }
    }

    fun removeFavorite(latlng: LatLng, place: String){
        if (favorites.containsKey(latlng)){
            favorites.remove(latlng)
            btnclicked = false
            Log.d(TAG, "removed favorite. Size of favorites list: " + favorites.count())
        }
    }

}