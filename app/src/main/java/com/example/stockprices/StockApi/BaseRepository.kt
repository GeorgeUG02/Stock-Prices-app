package com.example.stockprices.StockApi


import org.json.JSONObject

interface BaseRepository {
    suspend fun getData(ak:String,f:Pair<String,Int?>,s:String):JSONObject
}