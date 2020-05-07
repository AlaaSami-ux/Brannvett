package com.example.forestfire.viewModel

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import com.example.forestfire.R

class InfoViewModel : ViewModel() {

    fun baalDialog(context: Context){
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        builder.setIcon(R.drawable.ic_baal)
        builder.setTitle("Bål")
        builder.setMessage("I perioden 15. april til 15. " +
                "september er forbud mot å tenne bål, " +
                "grill og engangsgrill i skog eller i nærheten av skog. " +
                "I flere kommuner finnes det dog mulighet for å bruke bestemte grill- og " +
                "bålplasser under denne perioden. " +
                "OBS! Det kan alltid bli innført forbud hvis det trengs under sesongen. " +
                "Når du tenner bål er det ALLTID viktig å slukke skikkelig, " +
                "og forsikre seg om at det er helt slukket før du drar videre.\n" +
                "Kilde: norskfriluftsliv.no \n")
        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
        val dialog =builder.create()
        dialog.show()
       //dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFCCD1EF")))
    }

    fun glassDialog(context: Context){
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        builder.setIcon(R.drawable.ic_glassflasker)
        builder.setTitle("Glassflasker")
        builder.setMessage("Ikke bare er du et miljøsvin hvis du legger igjen søppel i naturen, " +
                "det kan også ha store konsekvenser. " +
                "En knust glassflaske kan med hjelp av solen potensielt starte en gressbrann, " +
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
        builder.setMessage("Når du bruker engangsgrill er det første du må tenke på underlaget. " +
                "Grillen skal stå stødig på et underlag som ikke er brennbart. " +
                "Bruk alltid stativet som følger med da engangsgrillene blir veldig varme på undersiden. " +
                "Pass alltid på at grillen er ordentlig slukket – bruk vann! " +
                "Og alltid kast den i en container spesifisert for engangsgriller.\n" +
                "Kilde: Brannvernforeningen.no\n")
        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
        val dialog =builder.create()
        dialog.show()
       // dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFCCD1EF")))
    }
    fun lynDialog(context: Context){
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        builder.setIcon(R.drawable.ic_lyn)
        builder.setTitle("Lyn")
        builder.setMessage("Tordenvær kan være ganske guffent. " +
                "Visste du at lynnedslag starter mange branner i skog i Norge i løpet av året? " +
                "Lyn slår ofte ned på steder det kan være utfordrende å komme seg til, " +
                "som gjør det enda vanskeligere for brannvesenet. " +
                "I tillegg kan det ligge og ulme i bakken i opptil tre dager før det antenner. " +
                "Alltid meld ifra hvis du ser antydning til skogbrann!\n")
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
                "I påsken 2020 fikk vi gjennom nyhetsbildet oppleve mange tilfeller hvor folk hadde kastet en varm engangsgrill i en vanlig søppelkasse, " +
                "og startet en alvorlig brann. Tenk før du kaster!")
        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
        val dialog =builder.create()
        dialog.show()
       // dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFCCD1EF")))
    }
    fun verktoyDialog(context: Context){
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        builder.setIcon(R.drawable.ic_verktoy)
        builder.setTitle("Verktøy")
        builder.setMessage("Bruk av verktøy som har åpen flamme, " +
                "eller som slår gnister kan være svært brannfarlig. Det er viktig å være obs på dette, " +
                "og ta de forholdsreglene som trengs. Gjør alltid en vurdering, " +
                "og utsett heller arbeidet hvis det er fare for skogbrann.\n\n" +
                "Kilde: bondelaget.no\n")
        builder.setPositiveButton("OK",{ dialogInterface: DialogInterface, i: Int -> })
        val dialog =builder.create()
        dialog.show()
        //dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFCCD1EF")))
    }
}