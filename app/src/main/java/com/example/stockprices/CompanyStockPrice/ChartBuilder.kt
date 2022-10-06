package com.example.stockprices.CompanyStockPrice

import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.setPadding
import com.example.stockprices.MainActivity
import com.example.stockprices.companiesList.CompaniesList
import com.example.stockprices.databinding.FragmentCompanyStockPriceBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

internal class ChartBuilder {
    private var lcp:ArrayList<Pair<Int,Int>> = arrayListOf()
    private var lcn:ArrayList<Pair<String,String>> = arrayListOf()
    private var lcsd:ArrayList<ArrayList<StockData>> = arrayListOf()
    private lateinit var activity:MainActivity
    private var binding:FragmentCompanyStockPriceBinding?=null
    fun build(formatter: SimpleDateFormat, p: Pair<String, Int?>,lcp:ArrayList<Pair<Int,Int>>,activity: MainActivity,binding: FragmentCompanyStockPriceBinding?,lcsd:ArrayList<ArrayList<StockData>>,lcn:ArrayList<Pair<String,String>>) {
        this.lcp=lcp
        this.activity=activity
        this.binding=binding
        val context = activity
        this.lcn = lcn
        this.lcsd = lcsd
        var i = this.lcp[0].second
        val f = this.lcp[0].first
        val lald = ArrayList<ArrayList<Date>>()
        val lalp = ArrayList<ArrayList<Float>>()
        val ald = ArrayList<Date>()
        val alp = ArrayList<Float>()
        lald.add(ald)
        lalp.add(alp)
        val cv = binding?.tl
        val cc = cv?.childCount
        if (cc != null && cc > 1) {
            activity.runOnUiThread {
                (cv as ViewGroup).removeViews(1, cc!! - 1)
            }
        }
        while (i >= f) {
            val d = formatter.parse(lcsd[0].get(i).date)
            ald.add(d)
            alp.add(lcsd[0].get(i).close.toFloat())
            val tlr = TableRow(context)
            val tv1 = TextView(context)
            tv1.setPadding(5)
            tv1.text = lcsd[0].get(i).date
            val tv2 = TextView(context)
            val tv3 = TextView(context)
            val tv4 = TextView(context)
            val tv5 = TextView(context)
            val tv6 = TextView(context)
            tv2.setPadding(5)
            tv3.setPadding(5)
            tv4.setPadding(5)
            tv5.setPadding(5)
            tv6.setPadding(5)
            tv2.text = lcsd[0].get(i).open.toString()
            tv3.text = lcsd[0].get(i).high.toString()
            tv4.text = lcsd[0].get(i).low.toString()
            tv5.text = lcsd[0].get(i).close.toString()
            tv6.text = lcsd[0].get(i).volume.toString()
            tlr.addView(tv1)
            tlr.addView(tv2)
            tlr.addView(tv3)
            tlr.addView(tv4)
            tlr.addView(tv5)
            tlr.addView(tv6)
            activity.runOnUiThread {
                binding?.tl?.addView(tlr)
            }
            i--

        }
        if (lcsd.size>1){
            for (j in 1..lcp.size-1) {
                var i = lcp[j].second
                val f = lcp[j].first
                val ald = ArrayList<Date>()
                val alp = ArrayList<Float>()
                lald.add(ald)
                lalp.add(alp)
                while (i >= f) {
                    val d = formatter.parse(lcsd[j].get(i).date)
                    ald.add(d)
                    alp.add(lcsd[j].get(i).close.toFloat())
                    i--
                }
            }
        }

        buildChart(p,lald,lalp)
    }

    private fun buildChart(
        p: Pair<String, Int?>,
        lald:ArrayList<ArrayList<Date>>,
        lalp:ArrayList<ArrayList<Float>>
    ) {
        val fd = lald[0].get(0).time
        val aldf = ArrayList<Float>()
        val laldf:ArrayList<ArrayList<Float>> = arrayListOf()
        laldf.add(aldf)
        aldf.add(0f)
        if (p.first == "minutes") {
            for (i in 1..lcp[0].second - lcp[0].first) {
                aldf.add(((lald[0].get(i).time - fd) / (1000 * 60)).toFloat())
            }
        } else if (p.first == "daily") {
            for (i in 1..lcp[0].second - lcp[0].first) {
                aldf.add(((lald[0].get(i).time - fd) / (1000 * 60 * 60 * 24)).toFloat())
            }
        }
        if (lald.size>1) {
            for (j in 1..lald.size-1) {
                val aldf = ArrayList<Float>()
                laldf.add(aldf)
                if (p.first == "minutes") {
                    for (i in 0..lcp[j].second - lcp[j].first) {
                        aldf.add(((lald[j].get(i).time - fd) / (1000 * 60)).toFloat())
                    }
                } else if (p.first == "daily") {
                    for (i in 0..lcp[j].second - lcp[j].first) {
                        aldf.add(((lald[j].get(i).time - fd) / (1000 * 60 * 60 * 24)).toFloat())
                    }
                }
            }
        }
        val le:ArrayList<ArrayList<Entry>> = arrayListOf()
        val lds:ArrayList<ILineDataSet> = arrayListOf()
        for (i in lcp.indices){
            le.add(arrayListOf())
            val e = le[i]
            for (j in lalp[i].indices) {
                e.add(Entry(laldf[i][j], lalp[i][j]))
            }
        }
        if (p.first == "minutes") {
            val lineDataSet = LineDataSet(
                le[0],
                "${lcn[0].second}" + " from " + lcsd[0].get(lcp[0].second).date + " with interval ${p.second} minutes"
            )
            lds.add(lineDataSet)
            for (i in 1..lcp.size-1){
                val lineDataSet = LineDataSet(
                le[i],
                    lcn[i].second
                )
                lds.add(lineDataSet)
            }
        } else if (p.first == "daily") {
             val lineDataSet = LineDataSet(
                le[0],
                "${lcn[0].second}" + " from " + lcsd[0].get(lcp[0].second).date + " daily"
            )
            lds.add(lineDataSet)
            for (i in 1..lcp.size-1){
                val lineDataSet = LineDataSet(
                    le[i],
                    lcn[i].second
                )
                lds.add(lineDataSet)
            }
        }
        for (i in lds.indices) {
            (lds[i] as LineDataSet).color = CompaniesList.colors[i]
        }
        val lineData = LineData(lds)
        activity.runOnUiThread {
            binding?.lineChart?.data = lineData
            binding?.lineChart?.invalidate()
        }
    }

     fun buildTitle(activity: MainActivity,binding: FragmentCompanyStockPriceBinding?) {
        val context = activity
        val tlr = TableRow(context)
        val tv1 = TextView(context)
        tv1.setPadding(5)
        tv1.text = "date"
        val tv2 = TextView(context)
        val tv3 = TextView(context)
        val tv4 = TextView(context)
        val tv5 = TextView(context)
        val tv6 = TextView(context)
        tv2.setPadding(5)
        tv3.setPadding(5)
        tv4.setPadding(5)
        tv5.setPadding(5)
        tv6.setPadding(5)
        tv2.text = "open"
        tv3.text = "high"
        tv4.text = "low"
        tv5.text = "close"
        tv6.text = "volume"
        tlr.addView(tv1)
        tlr.addView(tv2)
        tlr.addView(tv3)
        tlr.addView(tv4)
        tlr.addView(tv5)
        tlr.addView(tv6)
        activity.runOnUiThread {
            binding?.tl?.addView(tlr)
        }
    }
}