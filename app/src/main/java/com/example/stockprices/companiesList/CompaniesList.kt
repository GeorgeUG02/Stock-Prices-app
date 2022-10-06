package com.example.stockprices.companiesList

import android.graphics.Color
import com.example.stockprices.R

object CompaniesList {
    val cl:Array<Pair<String,Int>> = arrayOf(
        Pair("Apple", R.drawable.apple_logo),
        Pair("Amazon",R.drawable.amazon_logo),
        Pair("Google",R.drawable.google_logo),
        Pair("Microsoft", R.drawable.microsoft_logo),
        Pair("IBM", R.drawable.ibm_logo),
        Pair("AMD",R.drawable.amd_logo),
        Pair("Intel",R.drawable.intel_logo),
        Pair("Nvidia",R.drawable.nvidia_logo))
    val cst = mapOf<String,String>(Pair("Apple","AAPL"),Pair("Amazon","AMZN"),Pair("Google","GOOG"),Pair("Microsoft","MSFT"),Pair("IBM","IBM"),Pair("AMD","AMD"),Pair("Intel","INTC"),Pair("Nvidia","NVDA"))
    val maxCompanies = 7
    val colors : Array<Int> = arrayOf(Color.RED,Color.GREEN,Color.BLUE,Color.BLACK,Color.YELLOW,Color.GRAY,Color.CYAN)
}