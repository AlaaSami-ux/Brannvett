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
        val dag1: TextView = itemView.dag1
        val vær1: TextView = itemView.dag1
        val brannfare1: TextView = itemView.dag1
        val dag2: TextView = itemView.dag1
        val vær2: TextView = itemView.dag1
        val brannfare2: TextView = itemView.dag1
        val dag3: TextView = itemView.dag1
        val vær3: TextView = itemView.dag1
        val brannfare3: TextView = itemView.dag1
        fun bind(place: String){
            valgtSted.text = place
            dag1.text = "noe"
            Log.d("ListAdapter Adresse:", place)
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