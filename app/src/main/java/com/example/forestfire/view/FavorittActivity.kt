package com.example.forestfire.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.forestfire.R
import kotlinx.android.synthetic.main.activity_favoritt.*
import kotlinx.android.synthetic.main.activity_info.*
//import kotlinx.android.synthetic.main.activity_info.info2

class FavorittActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritt)
        /*
        goTilSettingActivity()
        goTilInfoActivity()
        goTilStartSidegActivity()

         */
    }
    /*
    fun goTilSettingActivity(){
        setting4.setOnClickListener{
            val intent =  Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }
    fun goTilInfoActivity() {
        info2.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }
    }
    fun goTilStartSidegActivity() {
        startSide4.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

     */
}
