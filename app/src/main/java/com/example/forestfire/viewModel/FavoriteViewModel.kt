package com.example.forestfire.viewModel


import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.forestfire.R
import com.google.android.gms.maps.model.LatLng
import java.io.*

class FavoriteViewModel : ViewModel(){

    val TAG = "FavoriteViewModel"
    var btnclicked = false
    private lateinit var context: Context

    var favoriteList: MutableList<LatLng> = ArrayList()
    var favorites: MutableMap<LatLng, String> = mutableMapOf()
    var favlat: MutableList<Double> = ArrayList() // list of latitudes
    var favlong: MutableList<Double> = ArrayList() // list of longitudes
    var favnames: MutableList<String> = ArrayList() // list of names
    fun setContext(c: Context){
        context = c
    }

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
            writeFile()
        }
    }

    fun removeFavorite(latlng: LatLng, place: String){
        if (favorites.containsKey(latlng)){
            favorites.remove(latlng)
            btnclicked = false
            Log.d(TAG, "removed favorite. Size of favorites list: " + favorites.count())
            writeFile()
        }
    }

    fun readFile() {
        // hente favoritter fra internal storage
        try {
            Log.d(TAG, "prøve å hente favoritter fra internal storage")
            val fisLat =
                context.openFileInput("favlat.ser")
            val oisLat = ObjectInputStream(fisLat)
            val fisLong =
                context.openFileInput("favlong.ser")
            val oisLong = ObjectInputStream(fisLong)
            val fisName =
                context.openFileInput("favname.ser")
            val oisName = ObjectInputStream(fisName)

            val savedFavorites: MutableMap<LatLng, String> = mutableMapOf()
            //Log.d(TAG, "favlat to string: " + favlat.toString())
            favlat = oisLat.readObject() as ArrayList<Double>
            favlong = oisLong.readObject() as ArrayList<Double>
            favnames = oisName.readObject() as ArrayList<String>
            savedFavoritesToHashMap()

            Log.d(TAG, "hentet favoritter fra minnet")
        } catch (e: FileNotFoundException){
            e.printStackTrace()
        } catch (e: NumberFormatException){
            e.printStackTrace()
        } catch (e: IOException){
            e.printStackTrace()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun writeFile() {
        favoritesToSerializable()
        // read hashmap to a file
        val favoriteLatLng = favorites.keys
        try {
            Log.d(TAG, "prøve å legge til favoritter i internal storage")
            val fosLat = context.openFileOutput("favlat.ser", Context.MODE_PRIVATE)
            val fosLong = context.openFileOutput("favlong.ser", Context.MODE_PRIVATE)
            val fosName = context.openFileOutput("favname.ser", Context.MODE_PRIVATE)
            val ooslat = ObjectOutputStream(fosLat)
            ooslat.writeObject(favlat)
            ooslat.close()
            val ooslong = ObjectOutputStream(fosLong)
            ooslong.writeObject(favlong)
            ooslong.close()
            val oosname = ObjectOutputStream(fosName)
            oosname.writeObject(favnames)
            oosname.close()
            Log.d(TAG, "lagt til favoritter i minnet")
        } catch (e: FileNotFoundException){
            e.printStackTrace()
        } catch (e: NumberFormatException){
            e.printStackTrace()
        } catch (e: IOException){
            e.printStackTrace()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun favoritesToSerializable(){
        val favoritesPos = favorites.keys
        favoritesPos.forEach {
            favlat.add(it.latitude)
            favlong.add(it.longitude)
        }
        favorites.values.forEach{
            favnames.add(it)
        }
    }

    fun savedFavoritesToHashMap(){
        var i: Int = 0
        val savedFavorites: MutableMap<LatLng, String> = mutableMapOf()
        favnames.forEach {
            val key = LatLng(favlat[i], favlong[i])
            savedFavorites[key] = it
            i += 1
        }
        favorites = savedFavorites
    }

}