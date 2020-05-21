package com.example.forestfire.view


import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
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
import com.example.forestfire.viewModel.fetchAPI.LocationForecastViewModel
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.element.view.*
import java.util.*

class ListAdapter(var forecastMap : HashMap<LatLng?, List<LocationForecastViewModel.FavForecast>>,
                  var posDangerMap : HashMap<LatLng?, List<String>>,
                  var favorites: MutableMap<LatLng, String>, var fragment: FavoritesFragment) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>()  {

    private var positions = favorites.keys // MutableSet of keys from favorites map
    private var places = favorites.values // MutableCollection of values from favorites map

    class ViewHolder constructor(itemView: View, val adapter: ListAdapter,
                                 val fragment: FavoritesFragment
    ) :

            RecyclerView.ViewHolder(itemView){
        val valgtSted: TextView = itemView.valgtSted

        val vaer_text1: TextView = itemView.vaer_text1
        val vaer_symbol1 : ImageView = itemView.vaer_symbol1
        val brann_index1 : TextView = itemView.brann_index1
        val brann_symbol1 : ImageView = itemView.brann_symbol1

        val vaer_text2: TextView = itemView.vaer_text2
        val vaer_symbol2 : ImageView = itemView.vaer_symbol2
        val brann_index2 : TextView = itemView.brann_index2
        val brann_symbol2 : ImageView = itemView.brann_symbol2

        val vaer_text3 : TextView = itemView.vaer_text3
        val vaer_symbol3 : ImageView = itemView.vaer_symbol3
        val brann_index3 : TextView = itemView.brann_index3
        val brann_symbol3 : ImageView = itemView.brann_symbol3

        val remove: ImageButton = itemView.remove


        @SuppressLint("SetTextI18n")
        fun bind(applicationContext: Context?, place: String, ll: LatLng,
                 forecastList: List<LocationForecastViewModel.FavForecast>?,
                 dangerList : List<String>?){

            val dialogClickListener =
                DialogInterface.OnClickListener { _, which ->
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
                builder.setMessage(fragment.requireContext().getString(R.string.onskerAaslette)
                        + valgtSted.text + fragment.requireContext().getString(R.string.fraFavoritter))
                builder.setNegativeButton(fragment.requireContext().getString(R.string.nei), dialogClickListener)
                builder.setPositiveButton(fragment.requireContext().getString(R.string.nei), dialogClickListener)
                val dialog =builder.create()
                dialog.show()
                val msgTxt = dialog.findViewById<View>(android.R.id.message)!! as TextView
                msgTxt.setTextColor(Color.GRAY)
            }
            valgtSted.text = place
            Log.d("ListAdapter Adresse", place)

            vaer_text1.text = "${forecastList?.get(0)?.temperature}°"
            Picasso.with(applicationContext)
                .load("https://in2000-apiproxy.ifi.uio.no/weatherapi/weathericon/1.1?content_type=image%2Fpng&symbol=${forecastList?.get(0)?.symbol_id}")
                .resize(70, 70)
                .into(vaer_symbol1)

            brann_index1.text = dangerList?.get(0)
            var brann : Int = getTree(dangerList?.get(0)!!.toInt())
            brann_symbol1.setImageResource(brann)


            vaer_text2.text = "${forecastList?.get(1)?.temperature}°"
            Picasso.with(applicationContext)
                .load("https://in2000-apiproxy.ifi.uio.no/weatherapi/weathericon/1.1?content_type=image%2Fpng&symbol=${forecastList?.get(1)?.symbol_id}")
                .resize(70, 70)
                .into(vaer_symbol2)

            brann_index2.text = dangerList[1]
            brann = getTree(dangerList[1].toInt())
            brann_symbol2.setImageResource(brann)

            vaer_text3.text = "${forecastList?.get(2)?.temperature}°"
            Picasso.with(applicationContext)
                .load("https://in2000-apiproxy.ifi.uio.no/weatherapi/weathericon/1.1?content_type=image%2Fpng&symbol=${forecastList?.get(2)?.symbol_id}")
                .resize(70, 70)
                .into(vaer_symbol3)

            brann_index3.text = dangerList[2]
            brann = getTree(dangerList[2].toInt())
            brann_symbol3.setImageResource(brann)
        }

        private fun getTree(danger_index : Int) : Int{
            val brann : Int
            if(danger_index < 30){
                brann = R.drawable.ic_brannfare_gronntre
            }else if(danger_index in 30..60){
                brann = R.drawable.ic_brannfare_gultre
            }else {
                brann = R.drawable.ic_brannfare_rodtre
            }
            return brann
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element, parent, false), this, fragment)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fragment.context?.applicationContext,
            places.elementAt(position), positions.elementAt(position),
            forecastMap[positions.elementAt(position)],
            posDangerMap[positions.elementAt(position)]
        )
    }

    fun removeItem(ll: LatLng){
        favorites.remove(ll)
    }
}