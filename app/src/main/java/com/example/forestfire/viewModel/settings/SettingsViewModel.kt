package com.example.forestfire.viewModel.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.preference.Preference
import java.util.*

@Suppress("DEPRECATION")
class SettingsViewModel: ViewModel() {

    fun setDarkMode(newValue : String){
        if(newValue == "true"){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


    // lage en mørk mode metode
    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
     fun darkModeSwitch(s: Switch, activ: FragmentActivity?) {
        val sharedPrefEdit =
            activ?.getSharedPreferences("save", Context.MODE_PRIVATE)?.edit()
        s.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefEdit?.putBoolean("value", false)
                sharedPrefEdit?.apply()
                s.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefEdit?.putBoolean("value", true)
                sharedPrefEdit?.apply()
                s.isChecked = false
            }
        }
    }

        //lage en språk metode (engelsk og norsk)
    fun setLocale(lang: String, activ: Activity) {
        val myLocale = Locale(lang)
        val res: Resources = activ.resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.locale = myLocale // deprecated in API 25. this is API 23
        res.updateConfiguration(conf, dm) // deprecated in API 25. this is API 23
        val refresh = Intent(activ, activ::class.java)
        activ.finish()
        activ.startActivity(refresh)
    }
}