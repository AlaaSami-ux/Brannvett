package com.example.forestfire.view

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.forestfire.R
import com.example.forestfire.viewModel.settings.SettingsViewModel
import java.util.*


class SettingsFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var root: View
    private lateinit var spinner: Spinner
    private lateinit var switch: Switch
    private var settingView: SettingsViewModel = SettingsViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_settings2, container, false)
        settingView.loadLocale(requireActivity())


        // kaller på dark, lys mode metoden
        switch = root.findViewById(R.id.darkSwitch)
        settingView.darkModeSwitch(switch,activity)

        // bytte språk
            spinner = root.findViewById(R.id.spinner2)
            spinner.onItemSelectedListener = this

        return root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        parentFragmentManager
            .beginTransaction()
            .detach(this)
            .attach(this)
            .commit()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
      //  Toast.makeText(activity, "Nothing Is Selected", Toast.LENGTH_LONG).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (val selectedItem = parent?.getItemAtPosition(position).toString()) {
            "English" -> {
                settingView.setLocate("no",requireActivity())
                // activity?.finish()
                // activity?.recreate()
                Toast.makeText(activity, "Change the language from settings device to English", Toast.LENGTH_LONG).show()
            }
            "Norsk" -> {
                settingView.setLocate("nb",requireActivity())
                // recreate(requireActivity())
                Toast.makeText(activity, "Endre språk fra innstillingsenhet til Norsk", Toast.LENGTH_LONG).show()
            }
            "Velg Språk" -> {
                onNothingSelected(parent)
            }
        }
    }
}