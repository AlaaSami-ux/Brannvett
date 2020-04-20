package com.example.forestfire.view

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.MotionEventCompat
import com.example.forestfire.R

class ShowFireIndex : AppCompatActivity(), View.OnTouchListener{
    val TAG = "ShowFireIndexActivity"
    private var MIN_DISTANCE = 100

    private var previousY: Float = 0F

    private lateinit var slideDown: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_show_fire_index)

        // fyll hele skjermen med "dialog" boks
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        // gjør bakgrunnen gjennomsiktig så kun cardview synes
        window.setBackgroundDrawableResource(android.R.color.transparent)

        slideDown = findViewById(R.id.slideDown)
        slideDown.setOnTouchListener(this)
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
                    if (previousY < event.y && event.y-previousY > MIN_DISTANCE) {
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