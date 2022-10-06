package com.example.stockprices.CompanyStockPrice

data class StockData (
    val date:String,
    val open:Double,
    val high:Double,
    val low:Double,
    val close:Double,
    val volume:Int
)