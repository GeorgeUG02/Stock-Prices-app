package com.example.stockprices.newCompanies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.stockprices.app.USERCOMPANIESVIEWMODEL
import com.example.stockprices.databinding.FragmentNewCompaniesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named

class NewCompaniesFragment:Fragment() {
    var binding: FragmentNewCompaniesBinding? = null
    private val scope = getKoin().createScope<NewCompaniesFragment>()
    private val model: NewCompaniesViewModel = scope.get(qualifier = named(USERCOMPANIESVIEWMODEL))
    private val adapter = NewCompaniesAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewCompaniesBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.uCompList?.adapter = adapter
        val cs = CoroutineScope(SupervisorJob())
        model.liveData.observe(viewLifecycleOwner,{
             adapter.setData(it)
        })
        binding?.addC?.setOnClickListener {
            if (binding!!.cName.text.toString()=="" || binding!!.cTicker.text.toString()==""){
                return@setOnClickListener
            }
            cs.launch {
                model.insertCompany(
                    Pair(
                        binding!!.cName.text.toString(),
                        binding!!.cTicker.text.toString()
                    )
                )
                requireActivity().runOnUiThread{
                    adapter.insertCompany(Pair(
                        binding!!.cName.text.toString(),
                        binding!!.cTicker.text.toString()
                    ))
                }
            }
        }
        binding?.removeC?.setOnClickListener {
            if (binding!!.cRTicker.text.toString()==""){
                return@setOnClickListener
            }
            cs.launch {
                model.deleteCompany(binding!!.cRTicker.text.toString())
                requireActivity().runOnUiThread{
                    adapter.deleteCompany(binding!!.cRTicker.text.toString())
                }
            }
        }
        cs.launch{
            model.getData()
        }
    }
}