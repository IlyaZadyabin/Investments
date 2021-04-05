package com.wallstreet.investments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.finnhub.api.apis.DefaultApi
import com.finnhub.api.infrastructure.ApiClient
import com.finnhub.api.models.StockCandles
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.wallstreet.investments.databinding.ActivityStockBinding
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class StockActivity : AppCompatActivity()  {

    private lateinit var linechart: LineChart
    private lateinit var stock : Stock
    private lateinit var binding : ActivityStockBinding

    companion object {
        const val SYMBOL = "symbol"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stock = StockData.allStocks[intent?.extras?.getString(SYMBOL).toString()]!!

        binding.changePrice.text = stock.getDeltaPriceString()
        if (stock.getDeltaPriceString().first() == '-') {
            binding.changePrice.setTextAppearance(R.style.day_delta_minus)
        }
        binding.currentPrice.text = "$${stock.currentPrice}"
        binding.buyButton.text = "Buy for $${stock.currentPrice}"

        linechart = binding.lineChart
        linechart.setNoDataText("")
        title = stock.name + "\n" + stock.symbol

        drawChart()
    }

    private fun setIcon(menuItem: MenuItem?) {
        if (menuItem == null)
            return

        menuItem.icon =
                if (stock.isFavourite)
                    ContextCompat.getDrawable(this, R.drawable.ic_favourite)
                else ContextCompat.getDrawable(this, R.drawable.ic_non_favourite)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.layout_menu, menu)
        val layoutButton = menu?.findItem(R.id.action_switch_layout)
        setIcon(layoutButton)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_layout -> {
                if (stock.isFavourite) {
                    StockData.removeFromFavourites(stock)
                } else {
                    StockData.addFavouriteStock(stock)
                }
                setIcon(item)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun drawChart() {
        var entries = ArrayList<Entry>()
        ApiClient.apiKey["token"] = "c1esv2f48v6vvsb41tcg"
        val apiClient = DefaultApi()
        var stockCandles: StockCandles
        val context = this

        CoroutineScope(Dispatchers.Main).launch {
            try {
                if (stock.stockCandles.isEmpty()) {
                    binding.chartLoader.visibility = View.VISIBLE

                    val stockCandlesRequest = GlobalScope.async {
                        apiClient.stockCandles(stock.symbol, "D", 1590996950,
                                1617024999, null)
                    }

                    stockCandles = stockCandlesRequest.await()
                    binding.chartLoader.visibility = View.INVISIBLE

                    val closedPrices = stockCandles.c
                    val timestamps = stockCandles.t

                    for (i in closedPrices!!.indices) {
                        stock.stockCandles.add(StockCandle(closedPrices[i], Date(timestamps!![i] * 1000L)))
                        entries.add(Entry(i.toFloat(), closedPrices[i]))
                    }
                } else {
                    entries = stock.stockCandles.mapIndexed { index, stockCandle ->
                        Entry(index.toFloat(), stockCandle.price)
                    } as ArrayList<Entry>
                }
                drawChartFromEntries(entries)
            } catch (e: Exception) {
                val thread = Thread {
                    runOnUiThread { Toast.makeText(context, "Api error: " + e.message, Toast.LENGTH_SHORT).show() }
                }
                thread.start()
            }
        }
    }

    private fun drawChartFromEntries(entries: ArrayList<Entry>) {
        //val dataset = LineDataSet(obtainEntries(), "")
        val dataset = LineDataSet(entries, "")
        dataset.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataset.setDrawFilled(true)
        dataset.fillDrawable = ContextCompat.getDrawable(this, R.drawable.gradient)
        dataset.setDrawHighlightIndicators(true)

        dataset.lineWidth = 1.5f
        dataset.circleRadius = 5f
        //dataset.color = R.color.red
        dataset.color = Color.parseColor("#000000")
        //dataset.color = R.color.black
        dataset.setCircleColor(R.color.black)
        dataset.setDrawCircleHole(false)
        dataset.setDrawCircles(false)
        dataset.setDrawValues(false)
        dataset.highLightColor = R.color.black
        dataset.highlightLineWidth = 1f
        dataset.setDrawHorizontalHighlightIndicator(false)
        //dataset.isHighlightEnabled = true
        val data = LineData(dataset)

        linechart.description.isEnabled = false
        linechart.data = data
        linechart.xAxis.setDrawGridLines(false)
        linechart.axisLeft.setDrawGridLines(false)
        linechart.xAxis.axisLineColor = R.color.black//top line
        linechart.xAxis.textColor = R.color.black
        linechart.axisLeft.axisLineColor = R.color.black//left line
        linechart.axisLeft.textColor = R.color.black
        linechart.axisRight.axisLineColor = R.color.black//right line
        linechart.axisRight.textColor = R.color.black
        linechart.setDrawBorders(false)
        linechart.setDrawGridBackground(false)
        linechart.isAutoScaleMinMaxEnabled = false

        val markerView = CustomMarkerView(this, R.layout.marker_view, stock.stockCandles)
        linechart.marker = markerView
        linechart.setTouchEnabled(true)

        // remove axis
        val leftAxis = linechart.axisLeft
        leftAxis.isEnabled = false
        val rightAxis = linechart.axisRight
        rightAxis.isEnabled = false

        val xAxis = linechart.xAxis
        xAxis.isEnabled = false

        // hide legend
        val legend = linechart.legend
        legend.isEnabled = false
        linechart.setViewPortOffsets(0f, 0f, 0f, 0f) //remove padding
        linechart.invalidate()
        linechart.animateXY(300, 300)
    }

}