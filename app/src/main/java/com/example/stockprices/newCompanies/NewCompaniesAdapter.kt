package com.example.stockprices.newCompanies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockprices.R
import com.example.stockprices.databinding.UserCompanyViewBinding

class NewCompaniesAdapter : RecyclerView.Adapter<NewCompaniesAdapter.ViewHolder>() {
    var binding:UserCompanyViewBinding?=null
    val al:ArrayList<Pair<String,String>> = arrayListOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewCompaniesAdapter.ViewHolder {
        binding = UserCompanyViewBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding?.root)
    }
    fun setData(al:ArrayList<Pair<String,String>>){
        this.al.clear()
        this.al.addAll(al)
        notifyDataSetChanged()
    }
    fun insertCompany(p:Pair<String,String>){
        this.al.add(p)
        notifyItemInserted(al.size-1)
    }
    fun deleteCompany(s:String){
        var i = 0
        while (i<al.size){
            if (al[i].second==s){
                al.removeAt(i)
                i--
            }
            i++
        }
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: NewCompaniesAdapter.ViewHolder, position: Int) {
        holder.bind(al[position])
    }

    override fun getItemCount(): Int {
        return al.size
    }

    class ViewHolder(v: View?) : RecyclerView.ViewHolder(v!!) {
        fun bind(p:Pair<String,String>){
            val tv = itemView.findViewById<TextView>(R.id.user_company_name)
            tv.text = "${p.first}(${p.second})"
        }
    }
}