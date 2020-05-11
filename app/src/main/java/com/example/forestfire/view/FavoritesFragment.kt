package com.example.forestfire.view

import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forestfire.R
import com.example.forestfire.viewModel.FavoriteViewModel
import com.example.forestfire.viewModel.MapsViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.io.IOException
import java.util.*


class FavoritesFragment : Fragment() {

    val TAG = "FavoritesFragment"

    private lateinit var viewAdapter: ListAdapter
    private lateinit var my_recycler_view: RecyclerView
    private lateinit var root: View
    private lateinit var noFavoritesTextBox: TextView

    // Legg til
    private lateinit var leggTilBtn: Button
    private lateinit var leggTil: CardView
    private lateinit var tilbake: Button
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    // Rediger


    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var favorites: MutableMap<LatLng, String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_favorites, container, false)

        // tilgang til favoriteViewModel
        favoriteViewModel = activity?.run {
            ViewModelProviders.of(this)[FavoriteViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        // tilgang til mapsViewModel
        mapsViewModel = activity?.run {
            ViewModelProviders.of(this)[MapsViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        leggTilBtn = root.findViewById(R.id.leggTilBtn)
        leggTil = root.findViewById(R.id.leggTil)
        tilbake = root.findViewById(R.id.tilbake)

        // Initialize google places
        Places.initialize(context!!, "AIzaSyD10fJ7iHSaVhairAHZnpuFcrm5fU4SFM4")
        // Create a new Places client instance
        var placesClient: PlacesClient = Places.createClient(context!!)

        // initialize autocompleteFragment
        autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG
        ))
        // set bounds for the results
        autocompleteFragment.setLocationBias(
            RectangularBounds.newInstance(
                LatLngBounds(
                    LatLng(58.019156, 2.141567), LatLng(71.399348, 33.442113)
                )
            )
        )
        autocompleteFragment.setCountries("NO")
        autocompleteFragment.setActivityMode(AutocompleteActivityMode.OVERLAY)

        autocompleteFragment.setOnPlaceSelectedListener(object :
            PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(TAG, "Place: " + place.name + ", " + place.id)
                /*chosenNewPlace(place.latLng!!)
                mapsViewModel.moveCam(mMap, activity!!.applicationContext, place.latLng, DEFAULT_ZOOM)
                valgtSted.text = place.name
                valgtSted2.text = place.name*/
                if (getAddressFromLocation(place.latLng!!.latitude, place.latLng!!.longitude) != null) {
                    getAddressFromLocation(place.latLng!!.latitude, place.latLng!!.longitude)?.let {
                        favoriteViewModel.addFavorite(place.latLng!!, it)
                    }
                }
                val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false)
                }
                ft.detach(this@FavoritesFragment).attach(this@FavoritesFragment).commit()
                leggTil.visibility = View.GONE
            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
            }
        })

        leggTilBtn.setOnClickListener {
            Log.d(TAG, "Legg til trykket på")
            leggTil.visibility = View.VISIBLE
        }
        tilbake.setOnClickListener {
            Log.d(TAG, "gå tilbake")
            leggTil.visibility = View.GONE
        }

        noFavoritesTextBox = root.findViewById(R.id.no_favorites)
        favorites= favoriteViewModel.favorites

        my_recycler_view = root.findViewById(R.id.my_recycler_view)
        initRecyclerView()


        return root
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) : String?{
        Log.d(TAG, "getAddressFromLocation")
        val geocoder = Geocoder(activity!!, Locale.ENGLISH)
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.isNotEmpty()) {
                val fetchedAddress: Address = addresses[0]
                val strAddress: String = fetchedAddress.getAddressLine(0)
                val sted: String = strAddress.split(",", ignoreCase=true, limit=0).first()
                Log.d(TAG, "sted: $sted")
                return sted
            } else { /* do nothing */ }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    private fun initRecyclerView(){
        my_recycler_view.apply{
            layoutManager = LinearLayoutManager(activity!!)
            viewAdapter = ListAdapter(favorites)
            if(favorites.count() >0){
                noFavoritesTextBox.visibility = View.GONE
            }
            adapter = viewAdapter

        }
    }
}
