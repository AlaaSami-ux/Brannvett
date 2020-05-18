package com.example.forestfire.view

import android.content.Context
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
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forestfire.R
import com.example.forestfire.viewModel.FavoriteViewModel
import com.example.forestfire.viewModel.MapsViewModel
import com.example.forestfire.viewModel.fetchAPI.LocationForecastViewModel
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
import java.io.FileInputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*


class FavoritesFragment : Fragment() {

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
    private lateinit var favorites: MutableMap<LatLng, String>

    private val forecastModel : LocationForecastViewModel by viewModels{ LocationForecastViewModel.InstanceCreator() }


    override fun onCreate(savedInstanceState: Bundle?) {
        // Her legges ting vi vil ha tilgang til hele tiden

        super.onCreate(savedInstanceState)
    }

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


        // datoer
        dag1 = root.findViewById(R.id.dag1)
        dag2 = root.findViewById(R.id.dag2)
        dag3 = root.findViewById(R.id.dag3)
        c = Calendar.getInstance()
        var dato = c.get(Calendar.DAY_OF_MONTH).toString() + "/" + (c.get(Calendar.MONTH)+1).toString()
        dag1.text = dato
        c.roll(Calendar.DATE, 1)
        dato = c.get(Calendar.DAY_OF_MONTH).toString() + "/" + (c.get(Calendar.MONTH)+1).toString()
        dag2.text = dato
        c.roll(Calendar.DATE, 1)
        dato = c.get(Calendar.DAY_OF_MONTH).toString() + "/" + (c.get(Calendar.MONTH)+1).toString()
        dag3.text = dato


        leggTilBtn = root.findViewById(R.id.leggTilBtn)
        leggTil = root.findViewById(R.id.leggTil)
        tilbake = root.findViewById(R.id.tilbake)

        // Initialize google places
        Places.initialize(requireContext(), "AIzaSyD10fJ7iHSaVhairAHZnpuFcrm5fU4SFM4")
        // Create a new Places client instance
        var placesClient: PlacesClient = Places.createClient(requireContext())

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
                favoriteViewModel.addFavorite(place.latLng!!, place.name!!)

                forecastModel.addFavoriteForecast(place.latLng)

                updateFragment()
              
                leggTil.visibility = View.GONE
                autocompleteFragment.setText("")
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

        readFile()

        if(!::favorites.isInitialized){
            favorites = favoriteViewModel.favorites
        }

        my_recycler_view = root.findViewById(R.id.my_recycler_view)
        initRecyclerView()

        /*
        redigerBtn = root.findViewById(R.id.redigerBtn)

        redigerBtn.setOnClickListener {
            Log.d(TAG, "rediger")
            if (favorites.count() > 0){
                favorittCard = my_recycler_view.findViewById(R.id.favorittCard)
                removeBtn = my_recycler_view.findViewById(R.id.remove)
            } else { /* do nothing */}

            //removeBtn.visibility = View.VISIBLE
            // vil endre marginStart
        }
         */

        return root
    }

    fun readFile(){
        // hente favoritter fra internal storage
        try {
            Log.d(TAG, "prøve å hente favoritter fra internal storage")
            val fileInputStream =
                FileInputStream("favorites.txt")
            val objectInputStream = ObjectInputStream(fileInputStream)
            favorites = objectInputStream.readObject() as MutableMap<LatLng, String>
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    fun writeFile(){
        // read hashmap to a file
        try {
            Log.d(TAG, "prøve å legge til favoritter i internal storage")

            val fos =
                requireContext().openFileOutput("favorites.txt", Context.MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)
            oos.writeObject(favorites)
            oos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getInstance() : FavoritesFragment{
        return this
    }

    fun updateFragment(){
        val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this@FavoritesFragment).attach(this@FavoritesFragment).commit()
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) : String?{
        Log.d(TAG, "getAddressFromLocation")
        val geocoder = Geocoder(requireActivity(), Locale.ENGLISH)
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
        if (getFragmentManager() != null) {
            getFragmentManager()
                ?.beginTransaction()
                ?.detach(this)
                ?.attach(this)
                ?.commit();
        }
    }


    private fun initRecyclerView(){
        my_recycler_view.apply{
            layoutManager = LinearLayoutManager(requireActivity())
            viewAdapter = ListAdapter(requireActivity(), requireActivity(), forecastModel, favorites, this@FavoritesFragment)
            if(favorites.count() >0){
                noFavoritesTextBox.visibility = View.GONE
            }
            adapter = viewAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "resuming app")
    }

    override fun onPause() {
        super.onPause()
        // her skal jeg legge ting som jeg vil bevare dersom brukeren
        // skulle avslutte appen og ikke komme tilbake
        // onPause blir kalt ved første indikasjon på at brukeren forlater appen

        Log.d(TAG, "onPause")

        // bevare listen med favoritter
        favorites = favoriteViewModel.favorites

        writeFile()
    }

    override fun onStop(){
        super.onStop()

        Log.d(TAG, "onStop")
        // read hashmap to a file
        try {
            Log.d(TAG, "prøve å legge til favoritter i internal storage")

            val fos =
                requireContext().openFileOutput("YourInfomration.ser", Context.MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)
            oos.writeObject(favorites)
            oos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy")
    }
}