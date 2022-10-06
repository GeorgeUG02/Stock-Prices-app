package com.example.stockprices.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
        androidContext(this@App)
           modules(
               Modules.application,
               Modules.stockpricesfragment,
               Modules.companylistfragment,
               Modules.usercompaniesfragment
           )
        }
    }
}