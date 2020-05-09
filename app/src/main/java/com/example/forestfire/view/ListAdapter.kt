package com.example.forestfire.view


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.forestfire.R
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.element.view.*

class ListAdapter(private var favorites: MutableMap<LatLng, String>) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>()  {

    private var positions = favorites.keys // MutableSet of keys from favorites map
    private var places = favorites.values // MutableCollection of values from favorites map

    class ViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val valgtSted: TextView = itemView.valgtSted
        fun bind(place: String){
            valgtSted.text = place
            Log.d("ListAdapter latlng", place)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element, parent, false))
    }

    override fun getItemCount(): Int {
        return places.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(places.elementAt(position))
    }
}