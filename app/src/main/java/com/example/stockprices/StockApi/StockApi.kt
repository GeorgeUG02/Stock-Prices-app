package com.example.stockprices.StockApi

import com.google.gson.JsonElement
import kotlinx.coroutines.Deferred
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {
    @GET("/query")
    fun getStockPrices(@Query("function") f:String, @Query("symbol") s:String, @Query("interval") i:String, @Query("outputsize") os:String, @Query("apikey") ak:String): Deferred<JsonElement>
    @GET("/query")
    fun getStockPrices(@Query("function") f:String, @Query("symbol") s:String, @Query("outputsize") os:String, @Query("apikey") ak:String): Deferred<JsonElement>
}
