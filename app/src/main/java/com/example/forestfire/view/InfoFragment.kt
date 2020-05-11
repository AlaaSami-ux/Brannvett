package com.example.forestfire.view

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProviders

import com.example.forestfire.R
import com.example.forestfire.viewModel.InfoViewModel


class InfoFragment : Fragment() {

    private lateinit var infoViewModel: InfoViewModel
    private lateinit var root: View
    private lateinit var baal: ImageButton
    private lateinit var engangsgrill: ImageButton
    private lateinit var glass: ImageButton
    private lateinit var lyn: ImageButton
    private lateinit var soppel: ImageButton
    private lateinit var verktoy: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_info2, container, false)

        infoViewModel = activity?.run {
            ViewModelProviders.of(this)[InfoViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        baal = root.findViewById(R.id.baal)
        engangsgrill = root.findViewById(R.id.engangsgrill)
        glass = root.findViewById(R.id.glass)
        lyn = root.findViewById(R.id.lyn)
        soppel = root.findViewById(R.id.soppel)
        verktoy = root.findViewById(R.id.verktoy)

        baal.setOnClickListener { infoViewModel.baalDialog(activity!!) }
        engangsgrill.setOnClickListener { infoViewModel.engangsgrillDialog(activity!!) }
        glass.setOnClickListener { infoViewModel.glassDialog(activity!!) }
        lyn.setOnClickListener { infoViewModel.lynDialog(activity!!) }
        soppel.setOnClickListener { infoViewModel.soppelDialog(activity!!) }
        verktoy.setOnClickListener { infoViewModel.verktoyDialog(activity!!) }

        return root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (getFragmentManager() != null) {
            getFragmentManager()
                ?.beginTransaction()
                ?.detach(this)
                ?.attach(this)
                ?.commit();
        }
    }
}
