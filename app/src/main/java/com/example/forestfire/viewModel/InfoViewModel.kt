package com.example.forestfire.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginLeft
import androidx.lifecycle.ViewModel
import com.example.forestfire.R
import com.example.forestfire.R.*
import com.example.forestfire.R.color.*
import com.example.forestfire.R.drawable.*
import kotlinx.android.synthetic.main.fragment_settings2.view.*

class InfoViewModel : ViewModel() {

    fun baalDialog(context: Context){
        val text = TextView(context)
        text.setText(string.baal)
        text.setPadding(30, 30, 20, 30)
        text.setCompoundDrawablesWithIntrinsicBounds(ic_baalicon_smaal,0,0,0)
        text.textSize = 24F
        val text2 = TextView(context)
        text2.setText(string.baalInfo)

        val builder = AlertDialog.Builder(context, style.CustomAlertDialog)
       // builder.setIcon(drawable.ic_baalicon)
        builder.setCustomTitle(text)
        builder.setMessage(string.baalInfo)
        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
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
        builder.setMessage("\nEn knust glassflaske kan med hjelp av solen potensielt starte en gressbrann, " +
                "som kan utvikle seg til en skogbrann. \n\nTa alltid med deg søppelet ditt hjem igjen!\n")
        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
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
        builder.setMessage("\nEn engangsgrill skal alltid stå stødig på et underlag som ikke er brennbart, " +
                "og husk alltid å bruke stativet som følger med. " +
                "\n\nPass på at grillen er ordentlig slukket og at du kaster den " +
                "i en container spesifiser for engangsgriller." +
                "\n\nKilde: Brannvernforeningen.no")
        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
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
        builder.setMessage("\nVisste du at lynnedslag starter mange branner i skog i Norge i løpet av året? " +
                "I tillegg kan det ligge og ulme i bakken i opptil tre dager før det antenner. " +
                "\n\nAlltid meld ifra hvis du ser antydninger til skogbrann!\n")
        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
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
        text.setCompoundDrawablesWithIntrinsicBounds(ic_soppelcontainer_smaal,0,0,0)
        text.textSize = 24F

        val builder = AlertDialog.Builder(context, style.CustomAlertDialog)
        //builder.setIcon(drawable.ic_soppelcontainer)
        builder.setCustomTitle(text)
        builder.setMessage("\nBruk alltid en avfallscontainer som er for engangsgrill etter du er ferdig å bruke den. " +
                "Ofte finnes det en oversikt over hvilke parker eller området som har disse på kommunen sine nettsider. " +
                "\n\nTenk før du kaster!")
        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
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
        text.setCompoundDrawablesWithIntrinsicBounds(ic_verktoy_small,0,0,0)
        text.textSize = 24F

        val builder = AlertDialog.Builder(context, style.CustomAlertDialog)
       // builder.setIcon(R.drawable.ic_verktoy)
        builder.setCustomTitle(text)
        builder.setMessage("\nBruk av verktøy som har åpen flamme," +
                " eller som slår gnister kan være svært brannfarlig. " +
                "Det er viktig å være obs på dette, og ta de forholdsreglene som trengs. " +
                "\n\nGjør alltid en vurdering, og utsett heller arbeidet hvis det er fare for skogbrann." +
                "\n\n Kilde: bondelaget.no")

        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
        val dialog =builder.create()
        dialog.show()
        val msgTxt = dialog.findViewById<View>(android.R.id.message)!! as TextView
        msgTxt.textSize = 22F
        msgTxt.setTextColor(Color.GRAY)

        //dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFCCD1EF")))
    }
}