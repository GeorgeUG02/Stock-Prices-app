package com.example.stockprices.companiesList

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.stockprices.CompanyStockPrice.CompanyStockPriceFragment
import com.example.stockprices.R
import com.example.stockprices.app.COMPANIESLISTVIEWMODEL
import com.example.stockprices.databinding.FragmentCompaniesListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
const val APP_PREFERENCES = "AppPreferences"
const val MAX_COMPANIES = "Max companies"
class CompaniesListFragment : Fragment() {
    var binding: FragmentCompaniesListBinding? = null
    private val scope = getKoin().createScope<CompaniesListFragment>()
    private val model: CompaniesListViewModel = scope.get(qualifier = named(COMPANIESLISTVIEWMODEL))
    val adapter = CompaniesListAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompaniesListBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.companiesList?.adapter = adapter
        model.liveData.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }
        val cs = CoroutineScope(SupervisorJob())
        cs.launch {
            model.getData()
        }
        binding?.chooseCompanies?.setOnClickListener {
            if (requireActivity().getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE).getInt(MAX_COMPANIES,0)==0){
                val e = requireActivity().getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE).edit()
                e.putInt(MAX_COMPANIES,CompaniesList.maxCompanies)
                e.apply()
            }
            val max_c = requireActivity().getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE).getInt(MAX_COMPANIES,0)
            if (adapter.set_c.size>0 && adapter.set_c.size<=max_c){
                val b = Bundle()
                val iter = adapter.set_c.iterator()
                var i=0
                while (iter.hasNext()){
                    val e = iter.next()
                    b.putString("cn_$i",e.first)
                    b.putString("ct_$i",e.second)
                    i++
                }
                requireActivity().supportFragmentManager.beginTransaction().addToBackStack("Company chart fragment").replace(R.id.view, CompanyStockPriceFragment().apply {
                    arguments = b
                }).commit()

            }
            else if (adapter.set_c.size>max_c){
                Toast.makeText(context,"Too many companies! Please, uncheck some.",Toast.LENGTH_LONG)
            }
            else {
                Toast.makeText(context,"No company is choosen! Please, choose at least one.",Toast.LENGTH_LONG)
            }
        }
    }

    override fun onDestroy() {
        binding = null
        adapter.onDestroy()
        super.onDestroy()
    }
}