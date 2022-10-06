package com.example.stockprices.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StockDataDB::class,CompanyDataDB::class,UserCompanyDataDB::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stockDao(): StockDAO
    abstract fun companyDao():CompanyDAO
    abstract fun usercompanyDao():UserCompanyDAO
}