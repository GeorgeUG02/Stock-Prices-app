package com.example.stockprices.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserCompanyDataDB (
    @PrimaryKey
    val c_id:Int?,
    val c_name:String,
    val c_ticker:String
    )