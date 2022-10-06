package com.example.stockprices.APIKeyChange

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.stockprices.companiesList.APP_PREFERENCES
import com.example.stockprices.databinding.FragmentChangeApiKeyBinding
const val APIKEY="API key"
class APIKeyChangeFragment : Fragment() {
    var binding: FragmentChangeApiKeyBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeApiKeyBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ak = requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE).getString(APIKEY,"")
        binding?.currentKey?.text = "Your API key is $ak"
        binding?.changeKey?.setOnClickListener {
            if (binding?.enterKey?.text.toString()==""){
                Toast.makeText(context,"Please, enter the API key!", Toast.LENGTH_LONG)
                return@setOnClickListener
            }
            val new_key = binding?.enterKey?.text.toString()
            val e = requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE).edit()
            e.putString(APIKEY,new_key)
            e.apply()
            binding?.currentKey?.text = "Your API key is $new_key"
        }
    }
}