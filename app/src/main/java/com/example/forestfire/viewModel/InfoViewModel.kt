package com.example.forestfire.viewModel

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import com.example.forestfire.R

class InfoViewModel : ViewModel() {

    fun baalDialog(context: Context){
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        builder.setIcon(R.drawable.ic_baal)
        builder.setTitle("Bål")
        builder.setMessage("I perioden 15.april til 15. september er det forbud mot å tenne bål," +
                " grill og engangsgrill i skog eller i nærheten av skog. " +
                "Når du tenner bål er det ALLTID viktig å slukke skikkelig, " +
                "og forsikre seg om at det er helt slukket før du drar videre. \n")
        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
        val dialog =builder.create()
        dialog.show()
        val msgTxt = dialog.findViewById<View>(android.R.id.message)!! as TextView
        msgTxt.setTextSize(20F)
    }

    fun glassDialog(context: Context){
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        builder.setIcon(R.drawable.ic_glassflasker)
        builder.setTitle("Glassflasker")
        builder.setMessage("En knust glassflaske kan med hjelp av solen potensielt starte en gressbrann, " +
                "som kan utvikle seg til en skogbrann. Ta alltid med deg søppelet ditt hjem igjen!\n")
        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
        val dialog =builder.create()
        dialog.show()
      //  dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFCCD1EF")))
    }
    fun engangsgrillDialog(context: Context){
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        builder.setIcon(R.drawable.ic_engangsgrill)
        builder.setTitle("Engangsgrill")
        builder.setMessage("En engangsgrill skal alltid stå stødig på et underlag som ikke er brennbart, " +
                "og husk alltid å bruke stativet som følger med. " +
                "Pass på at grillen er ordentlig slukket og at du kaster den " +
                "i en container spesifiser for engangsgriller. \n")
        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
        val dialog =builder.create()
        dialog.show()
       // dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFCCD1EF")))
    }
    fun lynDialog(context: Context){
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        builder.setIcon(R.drawable.ic_lyn)
        builder.setTitle("Lyn")
        builder.setMessage("Visste du at lynnedslag starter mange branner i skog i Norge i løpet av året? " +
                "I tillegg kan det ligge og ulme i bakken i opptil tre dager før det antenner. " +
                "Alltid meld ifra hvis du ser antydninger til skogbrann!\n")
        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
        val dialog =builder.create()
        dialog.show()
        //dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFCCD1EF")))
    }
    fun soppelDialog(context: Context){
        val builder = AlertDialog.Builder(context,R.style.CustomAlertDialog)
        builder.setIcon(R.drawable.ic_soppelcontainer)
        builder.setTitle("Avfallscontainere")
        builder.setMessage("Bruk alltid en avfallscontainer som er for engangsgrill etter du er ferdig å bruke den. " +
                "Ofte finnes det en oversikt over hvilke parker eller området som har disse på kommunen sine nettsider. " +
                "Tenk før du kaster!")
        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
        val dialog =builder.create()
        dialog.show()
       // dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFCCD1EF")))
    }
    fun verktoyDialog(context: Context){
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        builder.setIcon(R.drawable.ic_verktoy)
        builder.setTitle("Verktøy")
        builder.setMessage("Bruk av verktøy som har åpen flamme," +
                " eller som slår gnister kan være svært brannfarlig. " +
                "Det er viktig å være obs på dette, og ta de forholdsreglene som trengs. " +
                "Gjør alltid en vurdering, og utsett heller arbeidet hvis det er fare for skogbrann.\n")
        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
        val dialog =builder.create()
        dialog.show()
        //dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFCCD1EF")))
    }
}