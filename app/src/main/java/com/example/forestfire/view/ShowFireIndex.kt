package com.example.forestfire.view

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.MotionEventCompat
import com.example.forestfire.R
import com.example.forestfire.viewModel.FavoriteViewModel


class ShowFireIndex() : AppCompatActivity(), View.OnTouchListener {
    val TAG = "ShowFireIndexActivity"
    private var MIN_DISTANCE = 100

    private var previousY: Float = 0F

    private lateinit var slideDown: CardView
    private lateinit var favoriteBtn: ImageButton

    private val favoriteViewModel: FavoriteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_show_fire_index)

        // fyll hele skjermen med "dialog" boks
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        // gjør bakgrunnen gjennomsiktig så kun cardview synes
        window.setBackgroundDrawableResource(android.R.color.transparent)

        //slideDown = View.inflate(this, R.layout.activity_show_fire_index, null) as CardView
        slideDown = findViewById(R.id.slideDown)
        slideDown.setOnTouchListener(this)

        //favoriteBtn = findViewById(R.id.favoriteBtnOnMap)
        //slideDown.addView(favoriteBtn)

        // prøver å sjekke om knappen ble satt til favoritt i
        // mapsActivity, men har ikke fått det til å funke
        val mapsView: View = layoutInflater.inflate(R.layout.activity_main, null)
        var favoriteOrNot = mapsView.findViewById<ImageButton>(R.id.favoriteBtn)
        favoriteBtn = findViewById(R.id.favoriteBtn)
        if (favoriteOrNot.tag == "favorite"){
            Toast.makeText(this, "favoritt", Toast.LENGTH_LONG).show()
            Log.d(TAG, "favorite btn on map favorite tag")
            favoriteViewModel.buttonClick(favoriteBtn)
        } else if (favoriteOrNot.tag == "ikke favoritt"){
            Log.d(TAG, "favorite brn on map not favorite tag")
            Toast.makeText(this, "ikke favoritt", Toast.LENGTH_SHORT).show()
        }
        favoriteBtn.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "favorite button clicked")
            favoriteViewModel.buttonClick(favoriteBtn)
        })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // bør override performClick pga det er det blinde bruker når de bruker mobilen
        return super.onTouchEvent(event)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null) {
            val action = MotionEventCompat.getActionMasked(event) // finn ut av å fikse dette

            return when (action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d(TAG, "Action was DOWN")
                    previousY = event.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    Log.d(TAG, "Action was UP")
                    // swipe down
                    if (previousY < event.y && event.y - previousY > MIN_DISTANCE) {
                        //mDetector.onTouchEvent(event)
                        if (v != null && v.id == R.id.slideDown) {
                            this.finish()
                            return true
                        }
                    }
                    return false
                }
                else -> super.onTouchEvent(event)
            }
        }
        return false
    }
}