package com.example.stockprices.app

import androidx.room.Room
import com.example.stockprices.CompanyStockPrice.CompanyStockPriceFragment
import com.example.stockprices.CompanyStockPrice.CompanyStockPriceViewModel
import com.example.stockprices.StockApi.BaseRepository
import com.example.stockprices.StockApi.RepositoryImplAV
import com.example.stockprices.StockApi.StockApi
import com.example.stockprices.StockApi.StockRetrofit
import com.example.stockprices.Tests.TestAPI
import com.example.stockprices.Tests.TestRepository
import com.example.stockprices.companiesList.CompaniesList
import com.example.stockprices.companiesList.CompaniesListFragment
import com.example.stockprices.companiesList.CompaniesListViewModel
import com.example.stockprices.newCompanies.NewCompaniesFragment
import com.example.stockprices.newCompanies.NewCompaniesViewModel
import com.example.stockprices.room.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier
import org.koin.core.scope.get
import org.koin.dsl.module

const val RETROFITAPIAV = "Retrofit api AV"
const val STOCKPRICESVIEWMODEL = "Stock prices viewmodel"
const val REPOSITORYAV = "Repository AV"
const val TESTAPI = "Test API"
const val REPOSITORYTEST = "Repository TEST"
const val DATABASE = "database"
const val COMPANIESLISTVIEWMODEL = "Companies list ViewModel"
const val USERCOMPANIESVIEWMODEL = "User companies ViewModel"
object Modules {
    val application = module {
        single<StockApi>(qualifier = named(RETROFITAPIAV)) {
            StockRetrofit.getApi()
        }
        single<TestAPI>(qualifier = named(TESTAPI)) {
            TestAPI(androidContext())
        }
        single<BaseRepository>(qualifier = named(REPOSITORYAV)) {
            RepositoryImplAV(get(qualifier = named(RETROFITAPIAV)))
        }
        single<BaseRepository>(qualifier = named(REPOSITORYTEST)) {
            TestRepository(get(qualifier = named(TESTAPI)))
        }
        single<AppDatabase>(qualifier = named(DATABASE)) {
            Room.databaseBuilder(
                androidContext(),
                AppDatabase::class.java, "database"
            ).build()
        }
    }
    val stockpricesfragment = module {
        scope<CompanyStockPriceFragment> {
            viewModel(qualifier = named(STOCKPRICESVIEWMODEL)) {
                CompanyStockPriceViewModel()
            }
        }
    }
    val companylistfragment = module {
        scope<CompaniesListFragment>
        {
            viewModel(qualifier = named(COMPANIESLISTVIEWMODEL)) {
                CompaniesListViewModel(get(qualifier = named(DATABASE)), androidContext())
            }
        }
    }
    val usercompaniesfragment = module {
        scope<NewCompaniesFragment>
        {
            viewModel(qualifier = named(USERCOMPANIESVIEWMODEL)) {
                NewCompaniesViewModel(get(qualifier = named(DATABASE)))
            }
        }
    }
}