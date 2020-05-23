package com.example.forestfire.viewModel

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import com.example.forestfire.R.*
import com.example.forestfire.R.drawable.*

class InfoViewModel : ViewModel() {

    fun baalDialog(context: Context){
        val text = TextView(context)
        text.setText(string.baal)
        text.setPadding(30, 30, 20, 30)
        text.setCompoundDrawablesWithIntrinsicBounds(ic_baalicon_smaal,0,0,0)
        text.textSize = 24F
        val builder = AlertDialog.Builder(context, style.CustomAlertDialog)
       // builder.setIcon(drawable.ic_baalicon)
        builder.setCustomTitle(text)
        builder.setMessage(string.baalInfo)
        builder.setPositiveButton("OK") { _: DialogInterface, _: Int -> }
        val dialog =builder.create()
        dialog.show()
        val msgTxt = dialog.findViewById<View>(android.R.id.message)!! as TextView
        msgTxt.textSize = 22F
        msgTxt.setTextColor(Color.GRAY)
    }

    fun glassDialog(context: Context){
        val text = TextView(context)
        text.setText(string.glassflasker)
        text.setPadding(30, 30, 20, 30)
      //  text.setTextColor(Color.GRAY)
        text.setCompoundDrawablesWithIntrinsicBounds(ic_glassflaskericon_smaal,0,0,0)
        text.textSize = 24F

        val builder = AlertDialog.Builder(context, style.CustomAlertDialog)
      //  builder.setIcon(drawable.ic_glassflaskericon)
        builder.setCustomTitle(text)
        builder.setMessage(string.glassInfo)
        builder.setPositiveButton("OK") { _: DialogInterface, _: Int -> }
        val dialog =builder.create()
        dialog.show()
        val msgTxt = dialog.findViewById<View>(android.R.id.message)!! as TextView
        msgTxt.textSize = 22F
        msgTxt.setTextColor(Color.GRAY)

    }

    fun engangsgrillDialog(context: Context){
        val text = TextView(context)
        text.setText(string.engangsgrill)
        text.setPadding(30, 30, 20, 30)
       // text.setTextColor(Color.GRAY)
        text.setCompoundDrawablesWithIntrinsicBounds(ic_engangsgrill_ny_boks_smaal,0,0,0)
        text.textSize = 24F

        val builder = AlertDialog.Builder(context, style.CustomAlertDialog)
      //  builder.setIcon(drawable.ic_engangsgrill_ny_boks)
        builder.setCustomTitle(text)
        builder.setMessage(string.engangsgrillInfo)
        builder.setPositiveButton("OK") { _: DialogInterface, _: Int -> }
        val dialog =builder.create()
        dialog.show()
        val msgTxt = dialog.findViewById<View>(android.R.id.message)!! as TextView
        msgTxt.textSize = 22F
        msgTxt.setTextColor(Color.GRAY)

    }

    fun lynDialog(context: Context){
        val text = TextView(context)
        text.setText(string.lyn)
        text.setPadding(30, 30, 20, 30)
        //text.setTextColor(Color.GRAY)
        text.setCompoundDrawablesWithIntrinsicBounds(ic_lyn_ny_smaal,0,0,0)
        text.textSize = 24F

        val builder = AlertDialog.Builder(context, style.CustomAlertDialog)
        //builder.setIcon(drawable.ic_lyn_ny)
        builder.setCustomTitle(text)
        builder.setMessage(string.lynInfo)
        builder.setPositiveButton("OK") { _: DialogInterface, _: Int -> }
        val dialog =builder.create()
        dialog.show()
        val msgTxt = dialog.findViewById<View>(android.R.id.message)!! as TextView
        msgTxt.textSize = 22F
        msgTxt.setTextColor(Color.GRAY)

    }

    fun soppelDialog(context: Context){
        val text = TextView(context)
        text.setText(string.avfallscontainer)
        text.setPadding(30, 30, 20, 30)
        //text.setTextColor(Color.GRAY)
        text.setCompoundDrawablesWithIntrinsicBounds(ic_iconcontainer,0,0,0)
        text.textSize = 24F

        val builder = AlertDialog.Builder(context, style.CustomAlertDialog)
        //builder.setIcon(drawable.ic_soppelcontainer)
        builder.setCustomTitle(text)
        builder.setMessage(string.avfallInfo)
        builder.setPositiveButton("OK") { _: DialogInterface, _: Int -> }
        val dialog =builder.create()
        dialog.show()
        val msgTxt = dialog.findViewById<View>(android.R.id.message)!! as TextView
        msgTxt.textSize = 22F
        msgTxt.setTextColor(Color.GRAY)

    }

    fun verktoyDialog(context: Context){

        val text = TextView(context)
        text.setText(string.verktoy)
        text.setPadding(30, 30, 20, 30)
        //text.setTextColor(Color.GRAY)
        text.setCompoundDrawablesWithIntrinsicBounds(ic_toolicon_smaal,0,0,0)
        text.textSize = 24F

        val builder = AlertDialog.Builder(context, style.CustomAlertDialog)
       // builder.setIcon(R.drawable.ic_verktoy)
        builder.setCustomTitle(text)
        builder.setMessage(string.verktaayInfo)

        builder.setPositiveButton("OK") { _: DialogInterface, _: Int -> }
        val dialog =builder.create()
        dialog.show()
        val msgTxt = dialog.findViewById<View>(android.R.id.message)!! as TextView
        msgTxt.textSize = 22F
        msgTxt.setTextColor(Color.GRAY)
        //dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFCCD1EF")))
    }
}