package com.example.stockprices.Tests

import com.example.stockprices.StockApi.BaseRepository
import org.json.JSONObject

class TestRepository(val api:TestAPI):BaseRepository {
    override suspend fun getData(ak:String,f: Pair<String, Int?>, s: String): JSONObject {
        return api.getData(f,s)
    }
}