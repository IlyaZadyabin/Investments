package com.wallstreet.investments

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finnhub.api.apis.DefaultApi
import com.finnhub.api.infrastructure.ApiClient
import com.wallstreet.investments.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.math.RoundingMode
import kotlin.math.round

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    lateinit var adapter: StockAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerView

        adapter = StockAdapter(mutableListOf())
        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.Main).launch {
            if (StockData.stockList.isEmpty()) {
                val stocks = GlobalScope.async {
                    loadDefaultStocks()
                }
                StockData.stockList = stocks.await()
            }
            adapter = StockAdapter(StockData.stockList)
            recyclerView.adapter = adapter
        }

        binding.favoriteButton.setOnClickListener {
            if(!StockData.isFavouriteStockListEmpty() && !StockData.isStockListEmpty()) {
                if (!adapter.isFavouriteStocks) {
                    adapter.updateStockList(StockData.getFavouriteStocks())
                }
            }
        }

        binding.stocksButton.setOnClickListener {
            if(!StockData.isFavouriteStockListEmpty() && !StockData.isStockListEmpty()) {
                if (adapter.isFavouriteStocks) {
                    adapter.updateStockList(StockData.getNonFavouriteStocks())
                }
            }
        }

        binding.stockSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }
    private fun loadDefaultStocks() : MutableList<Stock> {
        val stockList = mutableListOf<Stock>()

        ApiClient.apiKey["token"] = "c1esv2f48v6vvsb41tcg"
        val apiClient = DefaultApi()
        try {
            var symbols = (apiClient.indicesConstituents("^NDX")).constituents!!
            symbols = symbols.subList(0, 10)
            for (symbol in symbols) {
                val quote = apiClient.quote(symbol)
                val companyProfile = apiClient.companyProfile2(symbol, null, null)
                stockList.add(Stock(
                        symbol,
                        companyProfile.name.toString(),
                        quote.c!!.toBigDecimal().setScale(3, RoundingMode.DOWN).toDouble(),
                        (quote.c!! - quote.pc!!).toBigDecimal().setScale(3, RoundingMode.DOWN).toDouble(),
                        false
                ))
            }
        } catch (e : Exception) {
            val thread = Thread {
                runOnUiThread { Toast.makeText(this, "Too much requests: " + e.message, Toast.LENGTH_SHORT).show() }
            }
            thread.start()
        }
        return stockList
    }
}
