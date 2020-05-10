package com.example.forestfire.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.forestfire.R
import com.example.forestfire.viewModel.FavoriteViewModel
import com.google.android.gms.maps.model.LatLng


class FavoritesFragment : Fragment() {

    private lateinit var viewAdapter: ListAdapter
    private lateinit var my_recycler_view: RecyclerView
    private lateinit var root: View
    private lateinit var noFavoritesTextBox: TextView

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favorites: MutableMap<LatLng, String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_favorites, container, false)

        // tilgang til favoriteViewModel
        favoriteViewModel = activity?.run {
            ViewModelProviders.of(this)[FavoriteViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        noFavoritesTextBox = root.findViewById(R.id.no_favorites)
        favorites= favoriteViewModel.favorites

        my_recycler_view = root.findViewById(R.id.my_recycler_view)
        initRecyclerView()


        return root
    }

    private fun initRecyclerView(){
        my_recycler_view.apply{
            layoutManager = LinearLayoutManager(activity!!)
            viewAdapter = ListAdapter(favorites)
            if(favorites.count() >0){
                noFavoritesTextBox.visibility = View.GONE
            }
            adapter = viewAdapter

        }
    }
}
