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
import com.example.forestfire.viewModel.FavoriteViewModel
import com.example.forestfire.viewModel.MapsViewModel
import com.example.forestfire.viewModel.UnitSystemViewModel
import com.example.forestfire.viewModel.fetchAPI.LocationForecastViewModel
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.element.view.*
import java.util.*

class ListAdapter(val context: Context,
                  var forecastMap : HashMap<LatLng?, List<LocationForecastViewModel.FavForecast>>,
                  var posDangerMap : HashMap<LatLng?, List<String>>,
                  var favorites: MutableMap<LatLng, String>,
                  var fragment: FavoritesFragment,
                  var favoriteViewModel: FavoriteViewModel,
                  var mapsViewModel: MapsViewModel,
                  var unitSystemViewModel : UnitSystemViewModel) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>()  {

    private var positions = favorites.keys // MutableSet of keys from favorites map
    private var places = favorites.values // MutableCollection of values from favorites map

    class ViewHolder constructor(val context: Context, itemView: View, val adapter: ListAdapter,
                                 val fragment: FavoritesFragment, val unitSystemViewModel : UnitSystemViewModel
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
                builder.setPositiveButton(fragment.requireContext().getString(R.string.ja), dialogClickListener)
                val dialog =builder.create()
                dialog.show()
                val msgTxt = dialog.findViewById<View>(android.R.id.message)!! as TextView
                msgTxt.setTextColor(Color.GRAY)
            }
            valgtSted.text = place

            @Suppress("DEPRECATION")
            // deprecated in API 29. this is API 23
            val preference = android.preference.PreferenceManager.getDefaultSharedPreferences(context)
            val selectedUnit = preference.getString("key_unit_valg", "Metric")

            //Dag 1

            //Temperatur
            if(selectedUnit == "Imperial"){
                vaer_text1.text = "${forecastList?.get(0)?.temperature?.toDouble()?.let {
                    unitSystemViewModel.toFahrenheit(
                        it
                    )
                }}°"
            }else {
                vaer_text1.text = "${forecastList?.get(0)?.temperature}°"
            }

            //Værsymbol
            Picasso.with(applicationContext)
                .load("https://in2000-apiproxy.ifi.uio.no/weatherapi/weathericon/1.1?content_type=image%2Fpng&symbol=${forecastList?.get(0)?.symbol_id}")
                .resize(60, 60)
                .into(vaer_symbol1)

            //Brannfare informasjon
            brann_index1.text = dangerList?.get(0)
            var brann : Int = getTree(dangerList?.get(0)!!.toInt())
            brann_symbol1.setImageResource(brann)


            //Dag 2

            //Temperatur
            if(selectedUnit == "Imperial"){
                vaer_text2.text = "${forecastList?.get(1)?.temperature?.toDouble()?.let {
                    unitSystemViewModel.toFahrenheit(
                        it
                    ) 
                }}°"
            }else {
                vaer_text2.text = "${forecastList?.get(1)?.temperature}°"
            }

            //Værsymbol
            Picasso.with(applicationContext)
                .load("https://in2000-apiproxy.ifi.uio.no/weatherapi/weathericon/1.1?content_type=image%2Fpng&symbol=${forecastList?.get(1)?.symbol_id}")
                .resize(60, 60)
                .into(vaer_symbol2)

            //Brannfare informasjon
            brann_index2.text = dangerList[1]
            brann = getTree(dangerList[1].toInt())
            brann_symbol2.setImageResource(brann)


            //Dag 3

            //Temperatur
            if(selectedUnit == "Imperial"){
                vaer_text3.text = "${forecastList?.get(2)?.temperature?.toDouble()?.let {
                    unitSystemViewModel.toFahrenheit(
                        it
                    )
                }}°"
            }else {
                vaer_text3.text = "${forecastList?.get(2)?.temperature}°"
            }

            //Værsymbol
            Picasso.with(applicationContext)
                .load("https://in2000-apiproxy.ifi.uio.no/weatherapi/weathericon/1.1?content_type=image%2Fpng&symbol=${forecastList?.get(2)?.symbol_id}")
                .resize(60, 60)
                .into(vaer_symbol3)

            //Brannfare informasjon
            brann_index3.text = dangerList[2]
            brann = getTree(dangerList[2].toInt())
            brann_symbol3.setImageResource(brann)
        }

        private fun getTree(danger_index : Int) : Int{
            return when {
                danger_index < 30 -> {
                    R.drawable.ic_brannfare_gronntre
                }
                danger_index in 30..60 -> {
                    R.drawable.ic_brannfare_gultre
                }
                else -> {
                    R.drawable.ic_brannfare_rodtre
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(context,
            LayoutInflater.from(parent.context).inflate(R.layout.element, parent, false),
            this,
            fragment,
            unitSystemViewModel)
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
        favoriteViewModel.removeFavorite(ll, mapsViewModel.getAddressFromLocation(ll.latitude, ll.longitude))
        favorites = favoriteViewModel.getFavorites() as MutableMap<LatLng, String>
        Log.d("ListAdapter", "antall favoritter etter removeItem: " + favorites.size)
    }
}