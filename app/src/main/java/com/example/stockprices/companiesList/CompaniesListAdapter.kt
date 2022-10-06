package com.example.stockprices.companiesList

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockprices.R
import com.example.stockprices.databinding.CompanyViewBinding


class CompaniesListAdapter() :
    RecyclerView.Adapter<CompaniesListAdapter.ViewHolder>() {
    var binding: CompanyViewBinding? = null
    val al: ArrayList<Triple<String, String, Bitmap?>> = arrayListOf()
    val set_c: HashSet<Pair<String,String>> = HashSet()
    fun setData(alin: ArrayList<Triple<String, String, Bitmap?>>) {
        set_c.clear()
        al.clear()
        al.addAll(alin)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompaniesListAdapter.ViewHolder {
        binding = CompanyViewBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding?.root)
    }

    override fun onBindViewHolder(holder: CompaniesListAdapter.ViewHolder, position: Int) {
        holder.bind(al[position],set_c)
    }

    override fun getItemCount(): Int {
        return al.size
    }

    fun onDestroy() {
        binding = null
    }

    class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        fun bind(p: Triple<String, String, Bitmap?>,set_c:HashSet<Pair<String,String>>) {
            if (p.third != null) {
                val im = itemView.findViewById<ImageView>(R.id.logo)
                im.setImageBitmap(p.third)
            }
            val tv = itemView.findViewById<TextView>(R.id.company_name)
            tv.setText("${p.first}(${p.second})")
            val cb = itemView.findViewById<CheckBox>(R.id.company_cb)
            cb.setOnClickListener{
                if ((it as CompoundButton).isChecked) {
                    //println("Checked")
                    set_c.add(Pair(p.first,p.second))
                } else {
                    //println("Un-Checked")
                    set_c.remove(Pair(p.first,p.second))
                }
            }
        }
    }

}