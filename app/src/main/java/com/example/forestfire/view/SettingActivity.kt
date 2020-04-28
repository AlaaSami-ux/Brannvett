package com.example.forestfire.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.forestfire.R
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_maps.info2
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)


        goTilInfoActivity()
        goTilFavorittActivity()
        goTilStartSidegActivity()
    }

    fun goTilInfoActivity(){
        info2.setOnClickListener{
            val intent =  Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }
    }
    fun goTilFavorittActivity() {
        favoritt2.setOnClickListener {
            val intent = Intent(this, FavorittActivity::class.java)
            startActivity(intent)
        }
    }
    fun goTilStartSidegActivity() {
        startside2.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }
}
