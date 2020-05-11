package com.example.forestfire.view

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.makeText
import androidx.lifecycle.ViewModelProviders

import com.example.forestfire.R
import com.example.forestfire.viewModel.settings.settingsViewModel
import kotlinx.android.synthetic.main.fragment_settings2.*
import java.util.*

class SettingsFragment() : Fragment() {
    val languageList = arrayOf("en", "no")
    private lateinit var settingsViewModel: settingsViewModel
    private lateinit var root: View
    private lateinit var spinner2: Spinner


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings2, container, false)

        switch3.setOnCheckedChangeListener{ buttonView, isChecked ->
       if (isChecked){
           Toast.makeText(getActivity(),"ON",Toast.LENGTH_LONG).show()
       }else{
           Toast.makeText(getActivity(),"OF ",Toast.LENGTH_LONG).show()
       }
        }
    }


}