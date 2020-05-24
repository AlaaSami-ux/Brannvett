package com.example.forestfire.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.forestfire.R
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager

class SplashActivity : AppCompatActivity() {

    // loading time for splash screen
    private val SPLASH_TIME_OUT:Long = 4000 //

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
        }
        return false
    }

    fun showNoConnectionDialog(){
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        builder.setMessage(getString(R.string.ingenInternett))
        builder.setPositiveButton(getString(R.string.lukkApp)) { _, _ ->
            ActivityCompat.finishAffinity(this)
        }
        val dialog =builder.create()
        dialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val darkmode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("key_dark_mode_switch", false)
        if(darkmode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            // sjekker om internett
            if (!isOnline(this)){
                Log.d("SplashActivity", "ingen internettilgang")
                showNoConnectionDialog()
            } else {
                // Starter app main activity
                startActivity(Intent(this,MainActivity::class.java))
                // ender denne activity
                finish()
            }
        }, SPLASH_TIME_OUT)
    }
}
