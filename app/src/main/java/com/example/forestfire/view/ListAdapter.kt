package com.example.forestfire.view


import android.content.DialogInterface
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.forestfire.R
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.element.view.*
import java.util.*

class ListAdapter(var favorites: MutableMap<LatLng, String>, var fragment: FavoritesFragment) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>()  {

    private var positions = favorites.keys // MutableSet of keys from favorites map
    private var places = favorites.values // MutableCollection of values from favorites map


    class ViewHolder constructor(itemView: View, adapter: ListAdapter, fragment:FavoritesFragment) :
            RecyclerView.ViewHolder(itemView){
        val adapter: ListAdapter = adapter
        val fragment: FavoritesFragment = fragment
        val valgtSted: TextView = itemView.valgtSted
        val vær1: ImageView = itemView.vær1
        val brannfare1: ImageView = itemView.brannfare1
        val vær2: ImageView = itemView.vær2
        val brannfare2: ImageView = itemView.brannfare2
        val vær3: ImageView = itemView.vær3
        val brannfare3: ImageView = itemView.brannfare3
        val remove: ImageButton = itemView.remove

        fun bind(place: String, ll: LatLng){


            var dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            // remove item
                            adapter.removeItem(ll)
                            fragment.updateFragment()
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {
                            // do nothing
                        }
                    }
                }
            remove.setOnClickListener {
                val builder = AlertDialog.Builder(itemView.rootView.context, R.style.CustomAlertDialog)
                builder.setIcon(R.drawable.ic_remove_circle_black_24dp)
                builder.setMessage("Vil du virkelig slette " + valgtSted.text + " fra dine favoritter?")
                builder.setNegativeButton("Nei", dialogClickListener)
                builder.setPositiveButton("Ja", dialogClickListener).show()
            }
            valgtSted.text = place
            Log.d("ListAdapter Adresse:", place)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element, parent, false), this, fragment)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(places.elementAt(position), positions.elementAt(position))
    }

    fun removeItem(ll: LatLng){
        favorites.remove(ll)
    }
}