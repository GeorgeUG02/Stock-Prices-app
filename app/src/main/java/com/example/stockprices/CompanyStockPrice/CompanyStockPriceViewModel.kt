package com.example.stockprices.CompanyStockPrice

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stockprices.StockApi.*
import com.example.stockprices.Tests.TestAPI
import com.example.stockprices.Tests.TestRepository
import com.example.stockprices.companiesList.CompaniesList
import org.json.JSONObject


class CompanyStockPriceViewModel(): ViewModel() {
    private val _liveData = MutableLiveData<ArrayList<JSONObject>>()
    val liveData: LiveData<ArrayList<JSONObject>> = _liveData
    suspend fun loadData(ak:String,lcn:ArrayList<Pair<String,String>>,repository: BaseRepository,p:Pair<String,Int?>){
        val alj = ArrayList<JSONObject>()
        for (i in lcn.indices) {
            val data = repository.getData(ak,p, lcn[i].second)
            alj.add(data)
        }
            _liveData.postValue(alj)
    }
}