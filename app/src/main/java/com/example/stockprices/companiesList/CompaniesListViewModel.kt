package com.example.stockprices.companiesList

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stockprices.room.AppDatabase
import com.example.stockprices.room.DataAccess
import java.io.ByteArrayOutputStream


class CompaniesListViewModel(db:AppDatabase, val context: Context):ViewModel() {
    private val _liveData = MutableLiveData<ArrayList<Triple<String,String,Bitmap?>>>()
    val liveData: LiveData<ArrayList<Triple<String,String,Bitmap?>>> = _liveData
    val da = DataAccess(db)
    suspend fun getData(){
        val l = da.loadFromDBC()
        val l2 = da.loadFromDBUC()
        val al= arrayListOf<Triple<String,String,Bitmap?>>()
        if (l.size >= CompaniesList.cl.size){
            for (i in l.indices){
                val bmp = BitmapFactory.decodeByteArray(l[i].c_logo, 0, l[i].c_logo.size)
                al.add(Triple(l[i].c_name,l[i].c_ticker,bmp))
            }

        }
        else{
            val al2= arrayListOf<Triple<String,String,ByteArray>>()
            for (i in CompaniesList.cl.indices){
                val bmp = BitmapFactory.decodeResource(context.resources,CompaniesList.cl[i].second)
                val stream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray: ByteArray = stream.toByteArray()
                al2.add(Triple(CompaniesList.cl[i].first, CompaniesList.cst[CompaniesList.cl[i].first]!!,byteArray))
                al.add(Triple(CompaniesList.cl[i].first, CompaniesList.cst[CompaniesList.cl[i].first]!!,bmp))
            }
            da.saveToDBC(al2)
        }
        for (i in l2.indices){
            al.add(Triple(l2[i].c_name,l2[i].c_ticker,null))
        }
        _liveData.postValue(al)
    }
}