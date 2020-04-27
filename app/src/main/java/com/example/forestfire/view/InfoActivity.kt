package com.example.forestfire.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.forestfire.R
import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_maps.info2
import kotlinx.android.synthetic.main.activity_setting.*

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        goTilSettingActivity()
        goTilFavorittActivity()
        goTilStartSidegActivity()
    }

    fun goTilSettingActivity(){
        setting3.setOnClickListener{
            val intent =  Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }
    fun goTilFavorittActivity() {
        favoritt3.setOnClickListener {
            val intent = Intent(this, FavorittActivity::class.java)
            startActivity(intent)
        }
    }
    fun goTilStartSidegActivity() {
        startSide3.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }
}
