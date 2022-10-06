package com.example.stockprices.Tests

import android.content.Context
import com.example.stockprices.R
import org.json.JSONObject

class TestAPI(val context: Context) {
    fun getData(f:Pair<String,Int?>,s:String):JSONObject{
        val s:String = context.getString(R.string.json)
        return JSONObject(s)
    }
}