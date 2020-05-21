package com.example.forestfire.viewModel.settings

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import java.util.*

class SettingsViewModel: ViewModel() {

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





     fun setLocate(Lang: String, activ: Activity) {
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()

        config.locale = locale // deprecated in API 24. This is API 23
        activ.baseContext?.resources?.updateConfiguration( // deprecated in API 25. this is API 23
            config,
            activ.baseContext.resources.displayMetrics
        )

        val editor = activ.getSharedPreferences("Settings", Context.MODE_PRIVATE)?.edit()
        editor?.putString("Spraak", Lang)
        editor?.apply()
    }


    fun loadLocale(activ: Activity){
        val pref : SharedPreferences? = activ.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language : String? = pref?.getString("Spraak","")
        if (language != null) {
            setLocate(language,activ)
        }
    }
}