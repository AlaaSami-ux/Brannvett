package com.example.forestfire.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate

import com.example.forestfire.R
import com.example.forestfire.viewModel.settings.settingsViewModel


class SettingsFragment() : Fragment() {
    val languageList = arrayOf("en", "no")
    private lateinit var settingsViewModel: settingsViewModel
    private lateinit var root: View
    private lateinit var spinner: Spinner
    private lateinit var switch: Switch
    private lateinit var switch2: Switch


    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root= inflater.inflate(R.layout.fragment_settings2, container, false)

        switch2 = root.findViewById(R.id.temp)
        switch2.setOnCheckedChangeListener{ buttonView, isChecked ->
       if (isChecked){
           Toast.makeText(activity,"ON",Toast.LENGTH_LONG).show()
       }else{
           Toast.makeText(activity,"OF ",Toast.LENGTH_LONG).show()
          }
        }

        switch = root.findViewById(R.id.darkSwitch)
/*
        val appSettingsprefs : SharedPreferences = activity?.getSharedPreferences("appSettingsPref",0)!!
        val sharedPrefEdit : SharedPreferences.Editor = appSettingsprefs.edit()
        val isNightModeOn : Boolean=appSettingsprefs.getBoolean("Nightmode",false)

        if (isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

 */
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        switch.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                switch.isChecked = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
             val sharedPrefEdit=  activity?.getSharedPreferences("Nightmode", Context.MODE_PRIVATE)?.edit()
                sharedPrefEdit?.putBoolean("Nightmode", false)
                sharedPrefEdit?.apply()

            } else {
                switch.isChecked = false
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                val sharedPrefEdit =activity?.getSharedPreferences("Nightmode", Context.MODE_PRIVATE)?.edit()
                sharedPrefEdit?.putBoolean("Nightmode", true)
                sharedPrefEdit?.apply()
            }
        }
        return root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

}