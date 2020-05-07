package com.example.forestfire.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.forestfire.R
import com.example.forestfire.viewModel.FavoriteViewModel
import com.example.forestfire.viewModel.MapsViewModel
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


class MapsFragment : Fragment(), OnMapReadyCallback {

    val TAG = "MapsActivity"
    private var DEFAULT_ZOOM = 15f
    private var MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1
    private var MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1
    private var MIN_DISTANCE = 100

    private lateinit var root: View
    private lateinit var mMap: GoogleMap
    private lateinit var weather: CardView
    private lateinit var valgtSted: TextView
    private lateinit var slideUp: CardView // the cardview that opens a new activity upon swipe up
    private lateinit var favoriteBtn: ImageButton // button for adding as favorite

    private var mLocationPermissionGranted = false // assume location permission is not granted

    // the location of the device
    private var deviceloc: LatLng? = null
    // Autocomplete fragment
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    //private var previousX: Float = 0F
    private var previousY: Float = 0F // used for checking if there has been a swipe upward

    // the ViewModel for the map
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_maps, container, false)

        // tilgang til mapsViewModel
        mapsViewModel = activity?.run {
            ViewModelProviders.of(this)[MapsViewModel::class.java]
        } ?: throw Exception("Invalid Activity")


        weather = root.findViewById(R.id.weather)


        valgtSted = root.findViewById(R.id.valgtSted)
        slideUp = root.findViewById(R.id.slideUp)
        //slideUp.setOnTouchListener(this)
        favoriteBtn = root.findViewById(R.id.favoriteBtn)
        /*favoriteBtn.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "favorite button clicked")
            favoriteViewModel.buttonClick(favoriteBtn)
        })*/

        // Try to obtain the map from the SupportMapFragment.
        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()
        mapFragment.getMapAsync(this)

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
                mapsViewModel.moveCam(mMap, activity!!.applicationContext, place.latLng, DEFAULT_ZOOM)
                mapsViewModel.addMarker(mMap, place.latLng)
                //valgtSted.text=place.name
            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
                Toast.makeText(activity!!.applicationContext, "Det har skjedd en feil", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        getLocationPermission()

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady: map is ready")
        mMap = googleMap

        // Converts 120 dip into its equivalent px
        val dip = 110f
        val r = resources
        val top = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        )

        mMap.setPadding(0, top.toInt(), 0, 0) // padding (left, top, right, bottom)
        mMap.setMinZoomPreference(10f) // jo lavere tall, jo lenger ut fra kartet kan man gå
        mMap.setMaxZoomPreference(20.0f) // hvor langt inn man kan zoome
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.isMyLocationEnabled = true

        if (mLocationPermissionGranted) {
            mapsViewModel.getDeviceLocation(mMap, activity!!.applicationContext)
            //valgtSted.text="Din posisjon"
        }
        // Create a LatLngBounds that includes the country Norway
        val norge = LatLngBounds(
            LatLng(58.019156, 2.141567), LatLng(71.399348, 33.442113)
        )
        // Constrain the camera target to the Norway bounds.
        mMap.setLatLngBoundsForCameraTarget(norge)
    }


    private fun getLocationPermission() {
        // get permission to get current location
        Log.d(TAG, "getLocationPermission called")
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "permission fine and coarse location granted")
                mLocationPermissionGranted = true
            } else {
                Log.d(TAG, "permission COARSE_LOCATION not granted")
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION
                )
            }
        } else {
            Log.d(TAG, "permission FINE_LOCATION not granted")
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                MY_PERMISSIONS_REQUEST_FINE_LOCATION
            )
        }
    }

}
