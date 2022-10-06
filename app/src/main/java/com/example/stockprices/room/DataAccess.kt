package com.example.stockprices.room

import com.example.stockprices.room.AppDatabase
import com.example.stockprices.room.StockDataDB
import org.json.JSONObject

class DataAccess(val db:AppDatabase) {
    var is_del = false
    suspend fun saveToDBSP(jo:JSONObject, ticker:String){
        if (!is_del) {
            db.stockDao().deleteStockData()
            is_del=true
        }
        val keys1 = jo.keys()
        while (keys1.hasNext()) {
            val key = keys1.next()
            db.stockDao().insertStockData(
                StockDataDB(
                    null, ticker,
                    key, (jo.get(key) as JSONObject).getDouble("1. open"),
                    (jo.get(key) as JSONObject).getDouble("2. high"),
                    (jo.get(key) as JSONObject).getDouble("3. low"),
                    (jo.get(key) as JSONObject).getDouble("4. close"),
                    (jo.get(key) as JSONObject).getInt("5. volume")
                )
            )
        }
    }
    suspend fun loadFromDBSP(ticker: String):List<StockDataDB> = db.stockDao().getStockData(ticker)
    suspend fun saveToDBC(al:ArrayList<Triple<String,String,ByteArray>>){
          for (i in al.indices){
              db.companyDao().insertCompanyData(CompanyDataDB(null,al[i].first,al[i].second,al[i].third))
          }
    }
    suspend fun loadFromDBC():List<CompanyDataDB> = db.companyDao().getAll()
    suspend fun saveToDBUC(p:Pair<String,String>){
            db.usercompanyDao().insertCompanyData(UserCompanyDataDB(null,p.first,p.second))
    }
    suspend fun loadFromDBUC():List<UserCompanyDataDB> = db.usercompanyDao().getAll()
    suspend fun deleteUC(ticker:String) = db.usercompanyDao().deleteCompany(ticker)
}