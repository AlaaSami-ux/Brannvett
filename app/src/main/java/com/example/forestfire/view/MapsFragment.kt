package com.example.forestfire.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.forestfire.R
import com.example.forestfire.viewModel.FavoriteViewModel
import com.example.forestfire.viewModel.MapsViewModel
import com.example.forestfire.viewModel.fetchAPI.LocationForecastViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.Tasks.await
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.squareup.picasso.Picasso
import java.io.IOException
import java.util.*


class MapsFragment : Fragment(),
    OnMapReadyCallback,
    View.OnTouchListener, View.OnClickListener{

    val TAG = "MapsFragment"
    private var MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1
    private var MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1
    private var MIN_DISTANCE = 100


    private lateinit var startLoc: LatLng
    private lateinit var lastLoc: LatLng
    private lateinit var lastLocName: String
    private var userLoc: LatLng? = null

    private lateinit var root: View
    private lateinit var weather: CardView
    private lateinit var wtext: TextView
    private lateinit var valgtSted: TextView
    private lateinit var valgtSted2: TextView
    private lateinit var stedinfo: View
    private lateinit var slideUp: CardView // the cardview that opens a new activity upon swipe up
    private lateinit var swipeUp: View
    private lateinit var favoriteBtn: ImageButton // button for adding as favorite
    private lateinit var favoriteBtn2: ImageButton // button for adding as favorite

    // Everything to do with maps
    private lateinit var mMap: GoogleMap
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    val norge = LatLngBounds(LatLng(58.019156, 2.141567), LatLng(71.399348, 33.442113))
    private val oslo = LatLng(59.911491, 10.757933)
    var dip = 90f
    private lateinit var search_box: CardView

    // kort som blir sveipet opp
    private lateinit var dag1: TextView
    private lateinit var dag2: TextView
    private lateinit var dag3: TextView
    private lateinit var c: Calendar


    //private var previousX: Float = 0F
    private var previousY: Float = 0F // used for checking if there has been a swipe upward

    // the ViewModels for map and favorites
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        // Her legges ting vi vil ha tilgang til hele tiden

        super.onCreate(savedInstanceState)
        // tilgang til mapsViewModel og favoriteViewModel
        mapsViewModel = activity?.run {
            ViewModelProviders.of(this)[MapsViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        favoriteViewModel = activity?.run {
            ViewModelProviders.of(this)[FavoriteViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        mapsViewModel.setActivity(requireActivity())
        mapsViewModel.setContext(requireContext())
        mapsViewModel.setFusedLocationProviderClient()
        //userLoc = mapsViewModel.getUserLocation() // userLoc may not be initialized

        // MapsViewModel holder kontroll på sist besøkte sted
        lastLoc = mapsViewModel.getLastUsedLocation() // lagre siste posisjon
        lastLocName = mapsViewModel.getLastUsedLocationName()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_maps, container, false)


        // initialize variables
        weather = root.findViewById(R.id.weather)
        wtext = root.findViewById(R.id.w_deg)
        slideUp = root.findViewById(R.id.slideUp)
        slideUp.setOnTouchListener(this)
        swipeUp = root.findViewById(R.id.swipeUp) // includer stedinfo
        swipeUp.setOnTouchListener(this)
        search_box = root.findViewById(R.id.search_box)

        valgtSted = root.findViewById(R.id.valgtSted) // tekst på cardView på forsiden
        valgtSted2 = swipeUp.findViewById<TextView>(R.id.valgtSted) // tekst på cardView når det er sveipet opp
        stedinfo = root.findViewById<View>(R.id.swipeUp) // cardView som synes når man har sveipet opp
        favoriteBtn = root.findViewById(R.id.favoritt)      // favorittknapp cardView nede
        favoriteBtn2 = stedinfo.findViewById(R.id.favoritt) // favorittknapp cardView åpent

        dag1 = stedinfo.findViewById(R.id.dag1)
        dag2 = stedinfo.findViewById(R.id.dag2)
        dag3 = stedinfo.findViewById(R.id.dag3)

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


        // Sjekke om sist brukte posisjon er en av favorittene til brukeren
        if (favoriteViewModel.isFavorite(lastLoc)){
            favoriteViewModel.setBtnClicked(favoriteBtn, favoriteBtn2)
        }

        // click listener to the two favorite buttons
        favoriteBtn.setOnClickListener(this)
        favoriteBtn2.setOnClickListener(this)

        // ------------------ Lets get the map going ------------------
        // Try to obtain the map from the SupportMapFragment.
        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()
        mapFragment.getMapAsync(this)

        // Initialize google places
        Places.initialize(requireContext(), "AIzaSyD10fJ7iHSaVhairAHZnpuFcrm5fU4SFM4")
        // Create a new Places client instance
        Places.createClient(requireContext())

        // initialize autocompleteFragment
        autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(
            Place.Field.NAME,
            Place.Field.LAT_LNG
        ))
        // set bounds for the results
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(norge))
        autocompleteFragment.setCountries("NO")
        autocompleteFragment.setActivityMode(AutocompleteActivityMode.OVERLAY)
        autocompleteFragment.setOnPlaceSelectedListener(object :
            PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(TAG, "Place: " + place.name + ", " + place.latLng)
                chosenNewPlace(place.latLng!!)
                mapsViewModel.setLastUsedLocation(place.latLng!!, place.name!!) // Oppdaterer sist brukte lokasjon
                mapsViewModel.moveCam(mMap, place.latLng!!)
                mapsViewModel.addMarker(mMap, place.latLng!!)
                settValgtStedTekst(place.name!!)
            }
            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
            }
        })
        // ---------------------------------------------------------------

        // get users permission to getlocation
        mapsViewModel.getLocationPermission()

        return root
    }

    override fun onClick(v: View){
        Log.d(TAG, "favorite button clicked")
        favoriteViewModel.changeFavoriteBoolean()
        if (favoriteViewModel.isBtnClicked()){ // hvis knappen er fylt med farge
            favoriteViewModel.addFavorite(lastLoc, valgtSted.text.toString())
            favoriteViewModel.setBtnClicked(favoriteBtn, favoriteBtn2)
        } else { // hvis knappen ikke er fylt med farge
            favoriteViewModel.removeFavorite(lastLoc, valgtSted.text.toString())
            favoriteViewModel.setBtnUnClicked(favoriteBtn, favoriteBtn2)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady: map is ready")
        mMap = googleMap

        // Coonvert dp to px
        val r = resources
        val top = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        )
        dip = 90f
        val bot = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        )

        mMap.setPadding(0, top.toInt(), 0, bot.toInt()) // padding (left, top, right, bottom)
        mMap.setMinZoomPreference(10f) // jo lavere tall, jo lenger ut fra kartet kan man gå
        mMap.setMaxZoomPreference(20.0f) // hvor langt inn man kan zoome
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.isMyLocationEnabled = true
        mMap.setOnMapLongClickListener {
            chosenNewPlace(it)
            mapsViewModel.addMarker(mMap, it)
            getAddressFromLocation(it.latitude, it.longitude)
            mapsViewModel.setLastUsedLocation(it)
            displayWeather(it)
        }


        mapsViewModel.moveCam(mMap, lastLoc) // Åpne kartet på sist brukte posisjon
        mapsViewModel.addMarker(mMap, lastLoc)
        settValgtStedTekst(lastLocName)
        //mapsViewModel.findDeviceLocation(mMap)
        displayWeather(lastLoc)

        mMap.setOnMyLocationButtonClickListener(OnMyLocationButtonClickListener {
            Log.d(TAG, "My location button clicked")
            mMap.myLocation
            getAddressFromLocation(mMap.myLocation.latitude, mMap.myLocation.longitude)
            mapsViewModel.addMarker(mMap, LatLng(mMap.myLocation.latitude, mMap.myLocation.longitude))
            mapsViewModel.moveCam(mMap, LatLng(mMap.myLocation.latitude, mMap.myLocation.longitude))
            mapsViewModel.setLastUsedLocation(LatLng(mMap.myLocation.latitude, mMap.myLocation.longitude))
            true
        })

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

    private fun chosenNewPlace(latlng: LatLng){
        lastLoc = latlng
        displayWeather(latlng)
        if (favoriteViewModel.isFavorite(latlng)){
            favoriteViewModel.setBtnClicked(favoriteBtn, favoriteBtn2)
        } else {
            favoriteViewModel.setBtnUnClicked(favoriteBtn, favoriteBtn2)
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val sted = mapsViewModel.getAddressFromLocation(latitude, longitude)
        settValgtStedTekst(sted)
    }

    private fun settValgtStedTekst(place: String){
        Log.d(TAG, "sett valgt sted til $place")
        valgtSted.text = place
        valgtSted2.text = place
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        // Jeg tror dette egentlig skal være i viewmodel men jeg vet ikke hvordan
        // må ha performclick for de med nedsatt syn
        if (event != null) {
            return when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d(TAG, "Action was DOWN")
                    previousY = event.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    Log.d(TAG, "Action was UP")
                    // swipe up
                    var shortAnimationDuration = resources.getInteger(android.R.integer.config_mediumAnimTime)
                    if (previousY > event.y && previousY - event.y > MIN_DISTANCE) {
                        if (v != null && v.id == R.id.slideUp) {
                            // fade INN det store kortet og bort været
                            swipeUp.apply{
                                alpha = 0f
                                visibility = View.VISIBLE
                                animate()
                                    .alpha(1f)
                                    .setListener(null)
                                    .duration = (shortAnimationDuration.toLong())
                            }
                            slideUp.animate() // fade UT det lille kortet
                                .alpha(0f)
                                .setListener(object: AnimatorListenerAdapter(){
                                    override fun onAnimationEnd(animation: Animator) {
                                        slideUp.visibility = View.GONE
                                    }
                                })
                                .duration = (shortAnimationDuration.toLong())
                            weather.animate()
                                .alpha(0f)
                                .setListener(object: AnimatorListenerAdapter(){
                                override fun onAnimationEnd(animation: Animator) {
                                    slideUp.visibility = View.GONE
                                }
                                })
                                .duration = (shortAnimationDuration.toLong())
                            search_box.animate()
                                .alpha(0f)
                                .setListener(object: AnimatorListenerAdapter(){
                                override fun onAnimationEnd(animation: Animator) {
                                    slideUp.visibility = View.GONE
                                }
                                })
                                .duration = (shortAnimationDuration.toLong())
                        }
                    } else if(previousY < event.y && event.y - previousY > MIN_DISTANCE){
                        if (v != null && v.id == R.id.swipeUp){
                            weather.visibility = View.VISIBLE
                            // fade INN det lille kortet og været
                            slideUp.apply{
                                alpha = 0f
                                visibility = View.VISIBLE
                                animate()
                                    .alpha(1f)
                                    .setListener(null)
                                    .duration = (shortAnimationDuration.toLong())
                            }
                            weather.apply{
                                alpha = 0f
                                visibility = View.VISIBLE
                                animate()
                                    .alpha(1f)
                                    .setListener(null)
                                    .duration = (shortAnimationDuration.toLong())
                            }
                            search_box.apply{
                                alpha = 0f
                                visibility = View.VISIBLE
                                animate()
                                    .alpha(1f)
                                    .setListener(null)
                                    .duration = (shortAnimationDuration.toLong())
                            }
                            swipeUp.animate() // fade UT det lille kortet
                                .alpha(0f)
                                .setListener(object: AnimatorListenerAdapter(){
                                    override fun onAnimationEnd(animation: Animator) {
                                        swipeUp.visibility = View.GONE
                                    }
                                })
                                .duration = (shortAnimationDuration.toLong())
                        }

                    }
                    return false
                }
                else -> return false
            }
        }
        return false
    }


    private val forecastModel by viewModels<LocationForecastViewModel> { LocationForecastViewModel.InstanceCreator() }

    private fun displayWeather(location : LatLng) {
        val tag = "displayWeather"
        Log.d(tag, location.toString())

        forecastModel.fetchLocationForecast(location)
        forecastModel.locationForecastLiveData.observe(viewLifecycleOwner, Observer {
            val temperature = it.product.time[0].location.temperature.value
            requireView().findViewById<TextView>(R.id.w_deg).text = "${temperature} \u2103"
            val id = it.product.time[1].location.symbol.number
            val img = requireView().findViewById<ImageView>(R.id.weather_icon)
            val url = "https://in2000-apiproxy.ifi.uio.no/weatherapi/weathericon/1.1?content_type=image%2Fpng&symbol=${id}"

            Picasso.with(activity)
                .load(url)
                .resize(100,100)
                .into(img)
        })
    }

}
