package com.example.forestfire.viewModel.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel

@Suppress("DEPRECATION")
class SettingsViewModel: ViewModel() {

    fun setDarkMode(newValue : String){
        if(newValue == "true"){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}