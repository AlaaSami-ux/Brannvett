package com.example.forestfire.view


import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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


class SettingsFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var root: View
    private lateinit var spinner: Spinner
    private lateinit var switch: Switch
    private var settingView: SettingsViewModel = SettingsViewModel()


    /*  override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null){
            savedInstanceState.getString("Spinn","")
        }
    }

   */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_settings2, container, false)

        spinner = root.findViewById(R.id.spinner2)
        switch = root.findViewById(R.id.darkSwitch)

        if (savedInstanceState != null){
            spinner.setSelection(savedInstanceState.getInt("Spinneren", 0))
        }

        // kaller på dark, lys mode metoden
        settingView.darkModeSwitch(switch,activity)

        // bytte språk
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
        when (parent?.getItemAtPosition(position).toString()) {
            "English" -> {
                settingView.setLocale("en", requireActivity())
                Toast.makeText(activity, "English", Toast.LENGTH_LONG).show()
            }
            "Norsk" -> {
                settingView.setLocale("nb", requireActivity())
                Toast.makeText(activity, "Norsk", Toast.LENGTH_LONG).show()
            }
            "Velg språk" -> {
                onNothingSelected(parent)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("FRAGMENT-A", "onSaveInstanceState")
        super.onSaveInstanceState(outState)
       outState.putInt("Spinneren", spinner.selectedItemPosition)
        //outState.putString("spinn",spinner.selectedItemPosition.toString())
    }

}