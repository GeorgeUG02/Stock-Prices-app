package com.example.stockprices.StockApi

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StockRetrofit {
    fun getApi():StockApi{
        return Retrofit.Builder().baseUrl("https://alphavantage.co").addCallAdapterFactory(CoroutineCallAdapterFactory()).addConverterFactory(GsonConverterFactory.create()).build().create(StockApi::class.java)
    }
}