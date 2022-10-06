package com.example.stockprices

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.stockprices.APIKeyChange.APIKeyChangeFragment
import com.example.stockprices.companiesList.CompaniesListFragment
import com.example.stockprices.databinding.ActivityMainBinding
import com.example.stockprices.newCompanies.NewCompaniesFragment

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (savedInstanceState==null) {
            supportFragmentManager.beginTransaction().addToBackStack("Companies list fragment").replace(R.id.view, CompaniesListFragment()).commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id){
            R.id.api_key -> {
                supportFragmentManager.beginTransaction().addToBackStack("API key fragment").replace(R.id.view, APIKeyChangeFragment()).commit()
            }
            R.id.add_remove_companies ->{
                supportFragmentManager.beginTransaction().addToBackStack("New companies fragment").replace(R.id.view,NewCompaniesFragment()).commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_menu,menu)
        return true
    }
    override fun onDestroy() {
        binding=null
        super.onDestroy()
    }
}