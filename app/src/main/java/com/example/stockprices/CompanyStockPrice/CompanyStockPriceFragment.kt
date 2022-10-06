package com.example.stockprices.CompanyStockPrice

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.stockprices.APIKeyChange.APIKEY
import com.example.stockprices.MainActivity
import com.example.stockprices.R
import com.example.stockprices.app.DATABASE
import com.example.stockprices.app.REPOSITORYAV
import com.example.stockprices.app.REPOSITORYTEST
import com.example.stockprices.app.STOCKPRICESVIEWMODEL
import com.example.stockprices.companiesList.APP_PREFERENCES
import com.example.stockprices.companiesList.CompaniesList
import com.example.stockprices.databinding.FragmentCompanyStockPriceBinding
import com.example.stockprices.room.AppDatabase
import com.example.stockprices.room.DataAccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import java.lang.Exception
import java.lang.Integer.min
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


class CompanyStockPriceFragment : Fragment() {
    private val builder: ChartBuilder = ChartBuilder()
    private var repository: String = ""
    private var lcn: ArrayList<Pair<String, String>> = arrayListOf()
    private var lcp: ArrayList<Pair<Int, Int>> = arrayListOf()
    private var lcsd: ArrayList<ArrayList<StockData>> = arrayListOf()
    private var ljo: ArrayList<JSONObject> = arrayListOf()
    private var keys: ArrayList<MutableIterator<String>> = arrayListOf()
    private var binding: FragmentCompanyStockPriceBinding? = null
    private var is_built = false
    private var isBlocked = false
    private var maxReached = false
    private var minReached = false
    private var numPoints = 10
    private val scope = getKoin().createScope<CompanyStockPriceFragment>()
    private val model: CompanyStockPriceViewModel =
        scope.get(qualifier = named(STOCKPRICESVIEWMODEL))
    private val db: AppDatabase = scope.get(qualifier = named(DATABASE))
    private val da = DataAccess(db)
    private var interval: Pair<String, Int?> = Pair("minutes", 5)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompanyStockPriceBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
            for (i in ljo.indices) {
                outState.putString("cn_$i", lcn[i].first)
                outState.putString("ct_$i", lcn[i].second)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            try {
                restoreData(savedInstanceState)
            } catch (e: Exception) {
                isBlocked = false
                Toast.makeText(requireContext(), "Error in restoring data!", Toast.LENGTH_LONG)
            }
        }
        val cs = CoroutineScope(SupervisorJob())
        if (lcn.size == 0) {
            var i = 0
            while (requireArguments().getString("cn_$i", "") != "") {
                lcn.add(
                    Pair(
                        requireArguments().getString("cn_$i", ""),
                        requireArguments().getString("ct_$i", "")
                    )
                )
                i++
            }
        }
        binding?.companyName?.text = "${lcn[0].first}(${lcn[0].second})"
        val scale = requireContext().getResources().getDisplayMetrics().density
        for (i in 1..lcn.size-1) {
            val v = View(requireContext())
            v.setBackgroundColor(CompaniesList.colors[i])
            v.layoutParams = ViewGroup.LayoutParams((30 * scale + 0.5f).toInt(),(30 * scale + 0.5f).toInt())
            val tv = TextView(requireContext())
            tv.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            tv.text = "${lcn[i].first}(${lcn[i].second})"
            binding?.loc?.addView(v)
            binding?.loc?.addView(tv)
        }

        val adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.time_series,
            android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.ts?.adapter = adapter

        model.liveData.observe(viewLifecycleOwner) {
            cs.launch {
                ljo = arrayListOf(JSONObject("{}"), JSONObject("{}"), JSONObject("{}"))
                try {
                    processData(it, cs)
                } catch (e: Exception) {
                    isBlocked = false
                    Toast.makeText(requireContext(), "Error in processing data!", Toast.LENGTH_LONG)
                }
            }
        }
        binding?.buildChart?.setOnClickListener {

            try {
                getData(cs, scope)
            } catch (e: Exception) {
                isBlocked = false
                Toast.makeText(requireContext(), "Error in receiving data!", Toast.LENGTH_LONG)
            }
        }
        binding?.saveData?.setOnClickListener {
            try {
                saveDataToDB(cs)
            } catch (e: Exception) {
                isBlocked = false
                Toast.makeText(requireContext(), "Error in saving data!", Toast.LENGTH_LONG)
            }
        }
        binding?.buildChartDb?.setOnClickListener {
            try {
                buildFromDB(cs)
            } catch (e: Exception) {
                isBlocked = false
                Toast.makeText(
                    requireContext(),
                    "Error in building chart with data from database!",
                    Toast.LENGTH_LONG
                )
            }
        }

    }

    private fun buildFromDB(cs: CoroutineScope) {
        if (!isBlocked) {
            cs.launch {
                isBlocked = true
                if (binding?.numPoints?.text != null && binding?.numPoints?.text.toString() != "" && binding?.numPoints?.text.toString()
                        .toInt() >= 10
                ) {
                    numPoints = binding?.numPoints?.text.toString().toInt()
                } else {
                    numPoints = 10
                }
                val stdl = da.loadFromDBSP(lcn[0].second)
                if (stdl.size == 0) {
                    isBlocked = false
                    return@launch
                }
                lcp.clear()
                lcp.add(Pair(0, min(numPoints, stdl.size - 1)))
                try {
                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val d1 = formatter.parse(stdl[0].date)
                    val d2 = formatter.parse(stdl[1].date)
                    val diff = (d1.time - d2.time) / (1000 * 60)
                    interval = Pair("minutes", diff.toInt())

                } catch (e: Exception) {
                    interval = Pair("daily", null)
                }
                lcsd.clear()
                lcsd.add(arrayListOf())
                for (j in 0..stdl.size - 1) {
                    lcsd[0].add(
                        StockData(
                            stdl[j].date,
                            stdl[j].open,
                            stdl[j].high,
                            stdl[j].low,
                            stdl[j].close,
                            stdl[j].volume
                        )
                    )
                }
                for (i in 1..lcn.size - 1) {
                    val stdl = da.loadFromDBSP(lcn[i].second)
                    if (stdl.size == 0) {
                        isBlocked = false
                        return@launch
                    }
                    lcsd.add(arrayListOf())
                    for (j in 0..stdl.size - 1) {
                        lcsd[i].add(
                            StockData(
                                stdl[j].date,
                                stdl[j].open,
                                stdl[j].high,
                                stdl[j].low,
                                stdl[j].close,
                                stdl[j].volume
                            )
                        )
                    }
                    lcp.add(
                        Pair(
                            0,
                            min(numPoints - 1, stdl.size - 1)
                        )
                    )
                }
                maxReached = true
                minReached = false
                requireActivity().runOnUiThread {
                    if (!is_built) {
                        builder.buildTitle(requireActivity() as MainActivity, binding)
                        is_built = true
                    }
                    if (interval.first == "minutes") {
                        builder.build(
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
                            interval,
                            lcp,
                            requireActivity() as MainActivity,
                            binding,
                            lcsd,
                            lcn
                        )
                    } else if (interval.first == "daily") {
                        builder.build(
                            SimpleDateFormat("yyyy-MM-dd"),
                            interval,
                            lcp,
                            requireActivity() as MainActivity,
                            binding,
                            lcsd,
                            lcn
                        )
                    }
                    binding?.scrl?.setOnClickListener {
                        onBackAVListener(cs)
                    }
                    binding?.scrr?.setOnClickListener {
                        onForwardAVListener(cs)
                    }
                }
                isBlocked = false
            }
        }
    }

    private fun saveDataToDB(cs: CoroutineScope) {
        if (!isBlocked) {
            if (repository == REPOSITORYAV && is_built) {
                cs.launch {
                    isBlocked = true
                    for (i in ljo.indices) {
                        da.saveToDBSP(ljo[i], lcn[i].second)
                    }
                    isBlocked = false
                }
            }
        }
    }

    private fun restoreData(savedInstanceState: Bundle) {
        lcn.add(
            Pair(
                savedInstanceState.getString("cn_0", ""),
                savedInstanceState.getString("ct_0", "")
            )
        )
    }

    private fun getData(cs: CoroutineScope, scope: Scope) {
        if (!isBlocked) {
            isBlocked = true
            val av = binding?.avApi?.isChecked
            val test = binding?.testApi?.isChecked
            var ak:String?
            if (av != null && av) {
                repository = REPOSITORYAV
                ak = requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE).getString(APIKEY,"")
                if (ak==""){
                    Toast.makeText(context,"Please, enter the API key!", Toast.LENGTH_LONG)
                    return
                }
            } else if (test != null && test) {
                repository = REPOSITORYTEST
                ak=""
            } else return
            val ts = binding?.ts?.selectedItem?.toString()!!
            var min: Int? = null
            if (ts == "daily") {
                min = null
            } else if (ts == "minutes") {
                if (binding?.minutes?.text != null && binding?.minutes?.text.toString() != "" && binding?.minutes?.text.toString()
                        .toInt() >= 1
                ) {
                    min = binding?.minutes?.text.toString().toInt()
                } else {
                    min = 5
                }
            }
            if (binding?.numPoints?.text != null && binding?.numPoints?.text.toString() != "" && binding?.numPoints?.text.toString()
                    .toInt() >= 10
            ) {
                numPoints = binding?.numPoints?.text.toString().toInt()
            } else {
                numPoints = 10
            }
            interval = Pair(ts, min)
            cs.launch {
                model.loadData(
                    ak!!,
                    lcn,
                    scope.get(qualifier = named(repository)),
                    interval
                )
            }
        }
    }

    private fun processData(it: ArrayList<JSONObject>, cs: CoroutineScope) {
        if (repository == REPOSITORYAV) {
            processDataAV(it, cs)
        } else if (repository == REPOSITORYTEST) {
            processDataTEST(it)
        }
    }

    private fun processDataAV(it: ArrayList<JSONObject>, cs: CoroutineScope) {
        val keys1 = it[0].keys()
        keys1.next()
        val ts = it[0].get(keys1.next()) as JSONObject
        ljo.clear()
        ljo.add(ts)
        keys = arrayListOf()
        keys.add(ljo[0].keys())
        lcsd.clear()
        lcsd.add(arrayListOf())
        val i = fillList(0)
        if (i <= 1) return
        lcp.clear()
        lcp.add(Pair(0, i - 1))
        for (j in 1..lcn.size - 1) {
            val keys1 = it[j].keys()
            keys1.next()
            val ts = it[j].get(keys1.next()) as JSONObject
            ljo.add(ts)
            keys.add(ljo[j].keys())
            lcsd.add(arrayListOf())
            val i = fillList(j)
            if (i <= 1) return
            lcp.add(Pair(0, i - 1))
        }
        maxReached = true
        minReached = false
        if (!is_built) {
            builder.buildTitle(requireActivity() as MainActivity, binding)
            is_built = true
        }
        if (interval.first == "minutes") {
            builder.build(
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
                interval,
                lcp,
                requireActivity() as MainActivity,
                binding,
                lcsd,
                lcn
            )
        } else if (interval.first == "daily") {
            builder.build(
                SimpleDateFormat("yyyy-MM-dd"),
                interval,
                lcp,
                requireActivity() as MainActivity,
                binding,
                lcsd,
                lcn
            )
        }
        requireActivity().runOnUiThread {
            binding?.scrl?.setOnClickListener {
                onBackAVListener(cs)
            }
            binding?.scrr?.setOnClickListener {
                onForwardAVListener(cs)
            }
        }
        isBlocked = false
    }

    private fun processDataTEST(it: ArrayList<JSONObject>) {
        val jo = it[0]
        val keys = jo.keys()
        val stockDataList = ArrayList<StockData>()
        while (keys.hasNext()) {
            val key = keys.next()
            stockDataList.add(
                StockData(
                    key,
                    (jo.get(key) as JSONObject).getDouble("myopen"),
                    0.0,
                    0.0,
                    (jo.get(key) as JSONObject).getDouble("myclose"),
                    0
                )
            )
        }
        lcp.clear()
        lcp.add(Pair(0, stockDataList.size - 1))
        lcsd.clear()
        lcsd.add(stockDataList)
        if (!is_built) {
            builder.buildTitle(requireActivity() as MainActivity, binding)
            is_built = true
        }
        builder.build(
            SimpleDateFormat("dd.MM.yyyy"),
            Pair("daily", null),
            lcp,
            requireActivity() as MainActivity,
            binding,
            lcsd,
            lcn
        )
        requireActivity().runOnUiThread {
            binding?.scrl?.setOnClickListener { }
            binding?.scrr?.setOnClickListener { }
            isBlocked = false
        }
    }

    private fun onForwardAVListener(cs: CoroutineScope) {
        if (isBlocked) return
        isBlocked = true
        if (lcp[0].first == 0) {
            maxReached = true
            isBlocked = false
            return
        }
        cs.launch {
            if (!maxReached) {
                if (minReached && (lcp[0].first > 0)) {
                    minReached = false
                }
                for (i in lcp.indices) {
                    lcp[i] = Pair(lcp[i].first - numPoints, lcp[i].first - 1)
                }
                if (lcp[0].first == 0) {
                    maxReached = true
                }
                builder.build(
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
                    interval,
                    lcp,
                    requireActivity() as MainActivity,
                    binding,
                    lcsd,
                    lcn
                )
            }
            isBlocked = false
        }

    }

    private fun onBackAVListener(cs: CoroutineScope) {
        if (isBlocked) return
        isBlocked = true
        if (!keys.isEmpty() && !keys[0].hasNext() && ((lcp[0].second + 1) == lcsd[0].size)) {
            minReached = true
            isBlocked = false
            return
        }
        if (!minReached) {
            if (((lcp[0].second + 1) == lcsd[0].size) && (!keys.isEmpty())) {
                for (j in lcp.indices) {
                    val i = fillList(j)
                    if (i <= 1) return
                    lcp[j] = Pair(lcp[j].second + 1, lcp[j].second + i)
                }
            }
            cs.launch {
                if (maxReached) {
                    maxReached = false
                }
                if ((lcp[0].second + 1) < lcsd[0].size) {
                    for (j in lcp.indices) {
                        lcp[j] = Pair(
                            lcp[j].second + 1,
                            min(
                                lcp[j].second + (lcp[j].second - lcp[j].first + 1),
                                lcsd[j].size - 1
                            )
                        )
                    }
                }
                builder.build(
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
                    interval,
                    lcp,
                    requireActivity() as MainActivity,
                    binding,
                    lcsd,
                    lcn,
                )
                isBlocked = false
            }
        }

    }

    private fun fillList(j: Int): Int {
        var i = 0
        while (keys[j].hasNext() && i <= numPoints - 1) {
            val key = keys[j].next()
            lcsd[j].add(
                StockData(
                    key,
                    (ljo[j].get(key) as JSONObject).getDouble("1. open"),
                    (ljo[j].get(key) as JSONObject).getDouble("2. high"),
                    (ljo[j].get(key) as JSONObject).getDouble("3. low"),
                    (ljo[j].get(key) as JSONObject).getDouble("4. close"),
                    (ljo[j].get(key) as JSONObject).getInt("5. volume")
                )
            )
            i++
        }
        return i
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}