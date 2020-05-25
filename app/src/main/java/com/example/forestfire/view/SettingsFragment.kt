package com.example.forestfire.view


import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.forestfire.R
import com.example.forestfire.viewModel.settings.SettingsViewModel


class SettingsFragment : PreferenceFragmentCompat() {

    private var settingVm: SettingsViewModel = SettingsViewModel()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val darkmode = findPreference<SwitchPreferenceCompat>("key_dark_mode_switch")
        darkmode?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener{ _, newValue ->
                settingVm.setDarkMode(newValue.toString())
                true
        }
    }
}