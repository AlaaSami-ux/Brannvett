package com.example.forestfire.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.forestfire.R
import android.content.Intent
import android.os.Handler

class SplashActivity : AppCompatActivity() {

    // loading time for splash screen
    private val SPLASH_TIME_OUT:Long = 4000 //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler().postDelayed({
            // Starter app main activity

            startActivity(Intent(this,MainActivity::class.java))

            // ender denne activity
            finish()
        }, SPLASH_TIME_OUT)
    }
}
