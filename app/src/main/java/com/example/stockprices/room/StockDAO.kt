package com.example.stockprices.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface StockDAO {
    @Query("SELECT * FROM StockDataDB WHERE comp = (:ticker)")
    suspend fun getStockData(ticker:String):List<StockDataDB>
    @Insert(onConflict = REPLACE)
    suspend fun insertStockData (stockDataDB: StockDataDB)
    @Query("DELETE FROM StockDataDB")
    suspend fun deleteStockData ()
}