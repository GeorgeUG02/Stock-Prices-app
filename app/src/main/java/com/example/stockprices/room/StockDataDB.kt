package com.example.stockprices.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StockDataDB(
    @PrimaryKey val st_id: Long?,
    val comp:String,
    val date:String,
    val open:Double,
    val high:Double,
    val low:Double,
    val close:Double,
    val volume:Int
)