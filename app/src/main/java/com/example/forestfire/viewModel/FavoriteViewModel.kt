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

    fun buttonClick(btn: ImageButton){
        Log.d(TAG, "favorite button clicked")
        if (!btnclicked){
            btn.setBackgroundResource(R.drawable.ic_favorite_black_24dp)
        } else {
            btn.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp)
        }
    }

    fun setBtnUnClicked(btn: ImageButton){
        btn.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp)
        btnclicked = false
    }

    fun setBtnClicked(btn: ImageButton){
        btn.setBackgroundResource(R.drawable.ic_favorite_black_24dp)
        btnclicked = true
    }

    fun changeFavoriteBoolean(){
        btnclicked = !btnclicked
    }

    fun isBtnClicked() : Boolean{
        return btnclicked
    }

    fun isFavorite(latlng: LatLng) : Boolean{
        return favorites.containsKey(latlng)
    }

    fun addFavorite(latlng: LatLng, place: String){
        // Her vil jeg legge inn LatLng som favoritt i en slags liste eller noe
        if(!favorites.containsKey(latlng)){
            favorites.put(latlng, place)
            btnclicked = true
            Log.d(TAG, "added favorite. Size of favorites list: " + favorites.count())
        }

        /*if (!favoriteList.contains(latlng)){ // sjekker om det allerede er favoritt
            favoriteList.add(latlng)
            btnclicked = true
            Log.d(TAG, "added favorite. Size of favorites list: " + favoriteList.size)
        }
         */
    }

    fun removeFavorite(latlng: LatLng, place: String){
        if (favorites.containsKey(latlng)){
            favorites.remove(latlng)
            btnclicked = false
            Log.d(TAG, "removed favorite. Size of favorites list: " + favorites.count())
        }
        /*if (favoriteList.contains(latlng)){ // sjekker om det allerede er favoritt
            favoriteList.remove(latlng)
            btnclicked = false
            Log.d(TAG, "removed favorite. Size of favorites list: " + favoriteList.size)
        }
         */
    }

}