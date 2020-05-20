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


class SettingsFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var languages = arrayOf("Norsk", "English")

    //  private lateinit var settingsViewModel: settingsViewModel
    private lateinit var root: View
    private lateinit var spinner: Spinner
    private lateinit var switch: Switch
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_settings2, container, false)

        darkModeSwitch()
        spinner = root.findViewById(R.id.spinner2)
        spinner.onItemSelectedListener = this

        return root
    }


    private fun setLocate(Lang: String) {
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

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun darkModeSwitch() {
        switch = root.findViewById(R.id.darkSwitch)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        switch.setOnCheckedChangeListener { compoundButton, b ->
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
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (fragmentManager != null) {
            fragmentManager
                ?.beginTransaction()
                ?.detach(this)
                ?.attach(this)
                ?.commit()
        }
    }

    fun updateFragment() {
        val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this@SettingsFragment).attach(this@SettingsFragment).commit()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
      //  Toast.makeText(activity, "Nothing Is Selected", Toast.LENGTH_LONG).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (val selectedItem = parent?.getItemAtPosition(position).toString()) {
            "English" -> {
                setLocate("en")
                Toast.makeText(activity, selectedItem, Toast.LENGTH_LONG).show()
            }
            "Norsk" -> {
                setLocate("nb")
                Toast.makeText(activity, selectedItem, Toast.LENGTH_LONG).show()
            }
            "Velg Språk" -> {
                onNothingSelected(parent)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        spinner = root.findViewById(R.id.spinner2)
        spinner.onItemSelectedListener = this
        }
}