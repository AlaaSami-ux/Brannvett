package com.example.forestfire.viewModel


import android.content.Context
import android.util.Log
import android.widget.ImageButton
import androidx.lifecycle.ViewModel
import com.example.forestfire.R
import com.google.android.gms.maps.model.LatLng
import java.io.*

class FavoriteViewModel : ViewModel(){

    val TAG = "FavoriteViewModel"
    var btnclicked = false
    private lateinit var context: Context

    private var favorites: MutableMap<LatLng, String> = mutableMapOf()
    private var favlat: MutableList<Double> = ArrayList() // list of latitudes
    private var favlong: MutableList<Double> = ArrayList() // list of longitudes
    private var favnames: MutableList<String> = ArrayList() // list of names

    fun setContext(c: Context){
        context = c
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
            favorites[latlng] = place
            btnclicked = true
            Log.d(TAG, "added favorite. Size of favorites list: " + favorites.count())
            writeFile()
        }
    }

    fun removeFavorite(latlng: LatLng, place: String){
        Log.d(TAG, "removeFavorite $place")
        if (favorites.containsKey(latlng)){
            favorites.remove(latlng)
            btnclicked = false
            Log.d(TAG, "removed favorite. Size of favorites list: " + favorites.count())
            writeFile()
        }
    }

    fun getFavorites(): Map<LatLng, String>{
        readFile()
        return favorites
    }

    fun readFile() {
        // hente favoritter fra internal storage
        try {
            Log.d(TAG, "Hente favoritter fra internal storage")
            val fisLat =
                context.openFileInput("favlat.ser")
            val oisLat = ObjectInputStream(fisLat)
            val fisLong =
                context.openFileInput("favlong.ser")
            val oisLong = ObjectInputStream(fisLong)
            val fisName =
                context.openFileInput("favname.ser")
            val oisName = ObjectInputStream(fisName)

            favlat.clear(); favlong.clear(); favnames.clear()
            @Suppress("UNCHECKED_CAST")
            favlat = oisLat.readObject() as ArrayList<Double>
            @Suppress("UNCHECKED_CAST")
            favlong = oisLong.readObject() as ArrayList<Double>
            @Suppress("UNCHECKED_CAST")
            favnames = oisName.readObject() as ArrayList<String>

            savedFavoritesToHashMap()
            Log.d(TAG, "hentet favoritter fra minnet")
            Log.d(TAG, "antall favoritter: " + favorites.size)
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
        Log.d(TAG, "favlat.size: " + favlat.size)
        try {
            Log.d(TAG, "reading " + favorites.size + " favorites to file")
            val fosLat = context.openFileOutput("favlat.ser", Context.MODE_PRIVATE)
            val fosLong = context.openFileOutput("favlong.ser", Context.MODE_PRIVATE)
            val fosName = context.openFileOutput("favname.ser", Context.MODE_PRIVATE)
            val ooslat = ObjectOutputStream(fosLat)
            ooslat.writeObject(favlat)
            fosLat.close()
            ooslat.flush()
            ooslat.close()
            val ooslong = ObjectOutputStream(fosLong)
            ooslong.writeObject(favlong)
            fosLong.close()
            ooslong.flush()
            ooslong.close()
            val oosname = ObjectOutputStream(fosName)
            oosname.writeObject(favnames)
            fosName.close()
            oosname.flush()
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
        favlat.clear(); favlong.clear(); favnames.clear()
        favoritesPos.forEach {
            favlat.add(it.latitude)
            favlong.add(it.longitude)
        }
        favorites.values.forEach{
            favnames.add(it)
        }
    }

    private fun savedFavoritesToHashMap(){
        var i = 0
        val savedFavorites: MutableMap<LatLng, String> = mutableMapOf()
        favnames.forEach {
            val key = LatLng(favlat[i], favlong[i])
            savedFavorites[key] = it
            i += 1
        }
        favorites = savedFavorites
    }

}