package com.example.stockprices.newCompanies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stockprices.room.AppDatabase
import com.example.stockprices.room.DataAccess

class NewCompaniesViewModel(db: AppDatabase):ViewModel() {
    val da = DataAccess(db)
    private val _liveData = MutableLiveData<ArrayList<Pair<String,String>>>()
    val liveData : LiveData<ArrayList<Pair<String,String>>> = _liveData
    suspend fun getData(){
       val l = da.loadFromDBUC()
       val al = ArrayList<Pair<String,String>>()
       for (i in l.indices){
           al.add(Pair(l[i].c_name,l[i].c_ticker))
       }
       _liveData.postValue(al)
    }
    suspend fun insertCompany(p:Pair<String,String>){
        da.saveToDBUC(p)
    }
    suspend fun deleteCompany(s:String){
        da.deleteUC(s)
    }
}