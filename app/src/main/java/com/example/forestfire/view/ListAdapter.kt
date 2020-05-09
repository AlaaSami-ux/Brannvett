package com.example.forestfire.view


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.forestfire.R
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.element.view.*

class ListAdapter(private var favorites: MutableList<LatLng>) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>()  {

    private var items = favorites


    class ViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val valgtSted = itemView.valgtSted

        fun bind(latlng: LatLng){
            valgtSted.text = latlng.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}