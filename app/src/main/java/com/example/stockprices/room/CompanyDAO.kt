package com.example.stockprices.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface CompanyDAO {
    @Query("SELECT * FROM CompanyDataDB")
    suspend fun getAll():List<CompanyDataDB>
    @Insert(onConflict = REPLACE)
    suspend fun insertCompanyData(c_d:CompanyDataDB)
    @Delete
    suspend fun deleteCompanyData(c_d: CompanyDataDB)
}