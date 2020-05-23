package com.example.forestfire.viewModel.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.example.forestfire.view.MainActivity
import com.example.forestfire.view.SettingsFragment
import java.util.*


class SettingsViewModel: ViewModel() {


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
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        val refresh = Intent(activ, activ::class.java)
        activ.finish()
        activ.startActivity(refresh)
    }
}