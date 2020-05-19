package com.example.forestfire.view

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentTransaction

import com.example.forestfire.R
import com.example.forestfire.viewModel.settings.settingsViewModel
import java.util.*


class SettingsFragment : Fragment() {
    private var languages = arrayOf("Norsk", "English")
    private lateinit var settingsViewModel: settingsViewModel
    private lateinit var root: View
    private lateinit var spinner: Spinner
    private lateinit var switch: Switch
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        loadLocate()
        root = inflater.inflate(R.layout.fragment_settings2, container, false)

        spinn()

        switch = root.findViewById(R.id.darkSwitch)

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        switch.setOnCheckedChangeListener { _, b ->
            if (b) {
                switch.isChecked = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                val sharedPrefEdit =
                    activity?.getSharedPreferences("Nightmode", Context.MODE_PRIVATE)?.edit()
                sharedPrefEdit?.putBoolean("Nightmode", false)
                sharedPrefEdit?.apply()
            } else {
                switch.isChecked = false
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                val sharedPrefEdit =
                    activity?.getSharedPreferences("Nightmode", Context.MODE_PRIVATE)?.edit()
                sharedPrefEdit?.putBoolean("Nightmode", true)
                sharedPrefEdit?.apply()
            }
        }
        return root
    }

    fun setLocate(Lang: String) {

        val locale = Locale(Lang)

        Locale.setDefault(locale)

        val config = Configuration()

        config.locale = locale // deprecated in API 24. This is API 23
        requireActivity().baseContext.resources.updateConfiguration( // deprecated in API 25. this is API 23
            config,
            requireActivity().baseContext.resources.displayMetrics
        )

        val editor = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", Lang)
        editor.apply()
    }

    private fun loadLocate() {
        val sharedPreferences = requireActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        language?.let { setLocate(it) }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    private fun spinn(){
        spinner = root.findViewById(R.id.spinner2)
        spinner.adapter = activity?.applicationContext?.let { ArrayAdapter(it, R.layout.support_simple_spinner_dropdown_item, languages) } as SpinnerAdapter
        spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                if (spinner != null) {
                    val type = parent?.getItemAtPosition(position).toString()
                    if (selectedItem == "English"){
                        setLocate("en")
                        Toast.makeText(activity, type, Toast.LENGTH_LONG).show()
                        println(type)
                    }else if (selectedItem == "Norsk"){
                        setLocate("nb")
                        Toast.makeText(activity, type, Toast.LENGTH_LONG).show()
                        println(type)
                    }
                }
            }
        }
    }

    fun updateFragment(){
        val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this@SettingsFragment).attach(this@SettingsFragment).commit()
    }
}