package com.example.forestfire.view

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
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
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
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

    private lateinit var chosenLoc: LatLng

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


    //private var previousX: Float = 0F
    private var previousY: Float = 0F // used for checking if there has been a swipe upward

    // the ViewModels for map and favorites
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_maps, container, false)

        // tilgang til viewmodels
        mapsViewModel = activity?.run {
            ViewModelProviders.of(this)[MapsViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        favoriteViewModel = activity?.run {
            ViewModelProviders.of(this)[FavoriteViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        mapsViewModel.setActivity(activity!!)
        mapsViewModel.setContext(context!!)

        // initialize variables
        weather = root.findViewById(R.id.weather)
        wtext = root.findViewById(R.id.wtext)
        slideUp = root.findViewById(R.id.slideUp)
        slideUp.setOnTouchListener(this)
        swipeUp = root.findViewById(R.id.swipeUp) // includer stedinfo
        swipeUp.setOnTouchListener(this)
        valgtSted = root.findViewById(R.id.valgtSted) // tekst på cardView på forsiden
        valgtSted2 = swipeUp.findViewById<TextView>(R.id.valgtSted) // tekst på cardView når det er sveipet opp

        stedinfo = root.findViewById<View>(R.id.swipeUp) // cardView som synes når man har sveipet opp

        favoriteBtn = root.findViewById(R.id.favoritt)      // favorittknapp cardView nede
        favoriteBtn2 = stedinfo.findViewById(R.id.favoritt) // favorittknapp cardView åpent

        var loc = mapsViewModel.getLastUsedLocation()   // MapsViewModel holder kontroll på sist besøkte sted

        if (favoriteViewModel.isFavorite(loc)){        // Om Oslo allerede er favoritt skal knappene fylles med farge
            favoriteViewModel.setBtnClicked(favoriteBtn, favoriteBtn2)
        }

        // click listener to the two favorite buttons
        favoriteBtn.setOnClickListener(this)
        favoriteBtn2.setOnClickListener(this)



        // --------------- Lets get the map going ---------------
        // Try to obtain the map from the SupportMapFragment.
        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()
        mapFragment.getMapAsync(this)

        // Initialize google places
        Places.initialize(context!!, "AIzaSyD10fJ7iHSaVhairAHZnpuFcrm5fU4SFM4")
        // Create a new Places client instance
        Places.createClient(context!!)

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
                mapsViewModel.setLastUsedLocation(place.latLng!!) // Oppdaterer sist brukte lokasjon
                mapsViewModel.moveCam(mMap, activity!!.applicationContext, place.latLng)
                settValgtStedTekst(place.name!!)
            }
            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
            }
        })

        mapsViewModel.getLocationPermission()

        return root
    }

    override fun onClick(v: View){
        Log.d(TAG, "favorite button clicked")
        favoriteViewModel.changeFavoriteBoolean()
        if (favoriteViewModel.isBtnClicked()){ // hvis knappen er fylt med farge
            favoriteViewModel.addFavorite(chosenLoc, valgtSted.text.toString())
            favoriteViewModel.setBtnClicked(favoriteBtn, favoriteBtn2)
        } else { // hvis knappen ikke er fylt med farge
            favoriteViewModel.removeFavorite(chosenLoc, valgtSted.text.toString())
            favoriteViewModel.setBtnUnClicked(favoriteBtn, favoriteBtn2)
        }
    }

    /*override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }*/

    private fun settValgtStedTekst(place: String){
        valgtSted.text = place
        valgtSted2.text = place
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady: map is ready")
        mMap = googleMap

        // Coonvert dp to px
        var dip = 90f
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
            getAddressFromLocation(it.latitude, it.longitude)
        }

        mMap.setOnMyLocationButtonClickListener {
            val myLoc = mapsViewModel.getDeviceLocation(mMap, activity!!.applicationContext)
            if (myLoc != null) {
                Log.d(TAG, "myLoc != null")
                getAddressFromLocation(myLoc.latitude, myLoc.longitude)
                chosenLoc = myLoc
                favoriteBtn.visibility = View.VISIBLE
                favoriteBtn2.visibility = View.VISIBLE
                displayWeather(chosenLoc)
            } else {
                valgtSted.text = "Din posisjon"
                valgtSted2.text = valgtSted.text
                favoriteBtn.visibility = View.GONE
                favoriteBtn2.visibility = View.GONE
            }
            false
        }

        chosenLoc = oslo
        mapsViewModel.moveCam(mMap, activity!!.applicationContext, oslo)
        mapsViewModel.addMarker(mMap, oslo)
        settValgtStedTekst("Oslo")
        displayWeather(chosenLoc)
        // Constrain the camera target to the Norway bounds.
        mMap.setLatLngBoundsForCameraTarget(norge)
    }

    private fun chosenNewPlace(latlng: LatLng){
        chosenLoc = latlng
        if (favoriteViewModel.isFavorite(latlng)){
            favoriteViewModel.setBtnClicked(favoriteBtn, favoriteBtn2)
        } else {
            favoriteViewModel.setBtnUnClicked(favoriteBtn, favoriteBtn2)
        }
        displayWeather(latlng)
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        var sted = mapsViewModel.getAddressFromLocation(latitude, longitude)
        settValgtStedTekst(sted)
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
            val img = requireView().findViewById<ImageView>(R.id.weater_icon)
            val url = "https://in2000-apiproxy.ifi.uio.no/weatherapi/weathericon/1.1?content_type=image%2Fpng&symbol=${id}"

            Picasso.with(activity)
                .load(url)
                .resize(100,100)
                .into(img)
        })
    }

}
