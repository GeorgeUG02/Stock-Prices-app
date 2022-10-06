package com.example.stockprices.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserCompanyDAO {
    @Query("SELECT * FROM UserCompanyDataDB")
    suspend fun getAll():List<UserCompanyDataDB>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyData(c_d:UserCompanyDataDB)
    @Query("DELETE FROM UserCompanyDataDB WHERE c_ticker = (:ticker)")
    suspend fun deleteCompany(ticker:String)
}