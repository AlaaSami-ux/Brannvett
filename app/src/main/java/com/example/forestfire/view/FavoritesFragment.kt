package com.example.forestfire.view

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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
import com.example.forestfire.viewModel.UnitSystemViewModel
import com.example.forestfire.viewModel.fetchAPI.FireDataViewModel
import com.example.forestfire.viewModel.fetchAPI.LocationForecastViewModel
import com.example.forestfire.viewModel.fetchAPI.StationInfoViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*
import kotlin.collections.HashMap


class FavoritesFragment(
    stationInfoViewModel: StationInfoViewModel,
    fireIndexViewModel: FireDataViewModel,
    locationForecastViewModel: LocationForecastViewModel
) : Fragment() {

    val TAG = "FavoritesFragment"

    private lateinit var viewAdapter: ListAdapter
    private lateinit var my_recycler_view: RecyclerView
    private lateinit var root: View
    private lateinit var noFavoritesTextBox: TextView

    // daoter
    private lateinit var c: Calendar
    private lateinit var dag1: TextView
    private lateinit var dag2: TextView
    private lateinit var dag3: TextView

    // Legg til
    private lateinit var leggTilBtn: ImageButton
    private lateinit var leggTil: CardView
    private lateinit var tilbake: Button
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var unitSystemViewModel: UnitSystemViewModel
    private lateinit var favorites: MutableMap<LatLng, String>

    private val fireViewModel = fireIndexViewModel
    private val stationViewModel = stationInfoViewModel
    private val forecastViewModel = locationForecastViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // tilgang til favoriteViewModel
        favoriteViewModel = activity?.run {
            ViewModelProviders.of(this)[FavoriteViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        // tilgang til mapsViewModel
        mapsViewModel = activity?.run {
            ViewModelProviders.of(this)[MapsViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        unitSystemViewModel = activity?.run {
            ViewModelProviders.of(this)[UnitSystemViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        favoriteViewModel.setContext(requireContext())
        favoriteViewModel.readFile() // hent brukerens faovritter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView")
        root = inflater.inflate(R.layout.fragment_favorites, container, false)

        if (!(activity as MainActivity).isOnline()) {
            (activity as MainActivity).showNoConnectionDialog()
        } else {

            favoriteViewModel.readFile() // hent brukerens faovritter
            favorites = favoriteViewModel.getFavorites() as MutableMap<LatLng, String>
            Log.d(TAG, "antall favoritter i onCreateView: " + favorites.size)
            // datoer
            dag1 = root.findViewById(R.id.dag1)
            dag2 = root.findViewById(R.id.dag2)
            dag3 = root.findViewById(R.id.dag3)
            c = Calendar.getInstance()
            var dato = c.get(Calendar.DAY_OF_MONTH)
                .toString() + "/" + (c.get(Calendar.MONTH) + 1).toString()
            dag1.text = dato
            c.roll(Calendar.DATE, 1)
            dato = c.get(Calendar.DAY_OF_MONTH)
                .toString() + "/" + (c.get(Calendar.MONTH) + 1).toString()
            dag2.text = dato
            c.roll(Calendar.DATE, 1)
            dato = c.get(Calendar.DAY_OF_MONTH)
                .toString() + "/" + (c.get(Calendar.MONTH) + 1).toString()
            dag3.text = dato

            leggTilBtn = root.findViewById(R.id.leggTilBtn)
            leggTil = root.findViewById(R.id.leggTil)
            tilbake = root.findViewById(R.id.tilbake)


            // Initialize google places
            Places.initialize(requireContext(), "AIzaSyD10fJ7iHSaVhairAHZnpuFcrm5fU4SFM4")
            // Create a new Places client instance
            Places.createClient(requireContext())
            // initialize autocompleteFragmentm
            autocompleteFragment =
                childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
            autocompleteFragment.setPlaceFields(
                listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG
                )
            )
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
                    Log.i(TAG, "valgt å legge til sted: " + place.name)
                    favoriteViewModel.addFavorite(place.latLng!!, place.name!!)
                    leggTil.visibility = View.GONE
                    updateFragment()
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

            if (favorites.isNotEmpty()) {
                noFavoritesTextBox.text = getString(R.string.lasterInn)
            }
            my_recycler_view = root.findViewById(R.id.my_recycler_view)
            forecastViewModel.fetchForecastFavorites(favorites.keys.toList())
            forecastViewModel.forecastFavoritesLiveData.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer Forecast@{ forecastMap ->
                    if (forecastMap == null) return@Forecast
                    Log.d("forecastViewModel ", "fetched Favs")

                    fireViewModel.fetchFireLocations()
                    fireViewModel.liveFireLocations.observe(
                        viewLifecycleOwner,
                        androidx.lifecycle.Observer Locations@{ dayList ->
                            if (dayList == null) return@Locations
                            Log.d("FireViewModel", "fetched all days")

                            stationViewModel.fetchData(dayList[0].locations)
                            stationViewModel.stationInfoLiveData.observe(
                                viewLifecycleOwner,
                                androidx.lifecycle.Observer Data@{
                                    if (it == null) return@Data
                                    Log.d("stationViewModel", "Filling hashmap")

                                    stationViewModel.fetchFavDanger(
                                        favorites.keys.toList(),
                                        dayList
                                    )
                                    stationViewModel.stationFavDangerLiveData.observe(
                                        viewLifecycleOwner,
                                        androidx.lifecycle.Observer Danger@{ posDangerMap ->
                                            if (posDangerMap == null) return@Danger
                                            Log.d("stationViewModel", "Fetched dangerlist of favs")
                                            initRecyclerView(forecastMap, posDangerMap)
                                        })
                                })

                        })
                })
        }
        return root
    }

    fun updateFragment() {
        Log.d(TAG, "updateFragment")
        val ft: FragmentTransaction = parentFragmentManager.beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this@FavoritesFragment).attach(this@FavoritesFragment).commit()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        parentFragmentManager
            .beginTransaction()
            .detach(this)
            .attach(this)
            .commit()
    }


    private fun initRecyclerView(
        forecastMap: HashMap<LatLng?, List<LocationForecastViewModel.FavForecast>>,
        posDangerMap: HashMap<LatLng?, List<String>>
    ) {
        Log.d(TAG, "initRecyclerView")
        my_recycler_view.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            viewAdapter = ListAdapter(requireContext(), forecastMap,
                posDangerMap,
                favorites,
                this@FavoritesFragment,
                favoriteViewModel,
                mapsViewModel,
                unitSystemViewModel)
            if (favorites.count() > 0) {
                noFavoritesTextBox.visibility = View.GONE
            }
            adapter = viewAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        favoriteViewModel.readFile()
        favorites = favoriteViewModel.getFavorites() as MutableMap<LatLng, String>
    }
}