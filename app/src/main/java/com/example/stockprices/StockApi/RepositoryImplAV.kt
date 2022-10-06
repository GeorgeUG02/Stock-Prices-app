package com.example.stockprices.StockApi

import com.google.gson.JsonObject
import org.json.JSONObject

const val FUNCTION = "TIME_SERIES_INTRADAY"
const val OS = "full"
class RepositoryImplAV(val api: StockApi):BaseRepository {
    override suspend fun getData(ak:String,f: Pair<String, Int?>, s:String):JSONObject {
        if (f.first=="minutes") {
            return JSONObject(
                (api.getStockPrices(FUNCTION, s, "${f.second!!}min", OS, ak)
                    .await() as JsonObject).toString()
            )
        }
        else if (f.first=="daily"){
            return JSONObject(
                (api.getStockPrices("TIME_SERIES_DAILY", s, OS, ak)
                    .await() as JsonObject).toString()
            )
        }
        else return JSONObject()
    }
}