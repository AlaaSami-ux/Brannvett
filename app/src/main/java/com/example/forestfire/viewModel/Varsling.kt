package com.example.forestfire.viewModel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.forestfire.R
import com.example.forestfire.view.InfoActivity

data class Varsling(val context: Context, val CHANNEL_ID: String) {
    var notificationManager : NotificationManager? = null

    fun vis_Varsel() {
        val notificationId:Int = 54
        val draTilResutat = Intent(context, InfoActivity::class.java)
        val pendingIntent =  PendingIntent
            .getActivity(context,0,draTilResutat, PendingIntent.FLAG_UPDATE_CURRENT)
            .apply { Intent.FLAG_ACTIVITY_CLEAR_TASK }
        val notification= NotificationCompat.Builder(context,CHANNEL_ID)
            .setContentTitle("Fare")
            .setContentText("Skogbrannfare på ditt favoritt sted")
            .setSmallIcon(R.drawable.ic_hot_blond_lady_24dp)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager?.notify(notificationId,notification)
    }
    fun createNotificationChannel(id:String, name:String, channelDiscription : String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel: NotificationChannel = NotificationChannel(id,name,importance).apply { description =channelDiscription }
            notificationManager?.createNotificationChannel(channel)
        }
    }
}