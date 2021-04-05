package com.wallstreet.investments

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.finnhub.api.apis.DefaultApi
import com.finnhub.api.infrastructure.ApiClient
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.wallstreet.investments.databinding.ActivityDetailBinding
import kotlinx.coroutines.*
import kotlinx.serialization.descriptors.PrimitiveKind
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailActivity : AppCompatActivity() {

    private lateinit var linechart: LineChart
    private var isFavourite : Boolean = false
    private lateinit var name : String
    private lateinit var symbol : String
    private lateinit var currentPrice : String
    private lateinit var changePrice : String

    companion object {
        val NAME = "name"
        val SYMBOL = "symbol"
        val CHANGE_PRICE = "changePrice"
        val CURRENT_PRICE = "currentPrice"
        val IS_FAVOURITE = "isFavourite"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        name = intent?.extras?.getString(NAME).toString()
        symbol = intent?.extras?.getString(SYMBOL).toString()
        currentPrice = intent?.extras?.getString(CURRENT_PRICE).toString()
        changePrice = intent?.extras?.getString(CHANGE_PRICE).toString()
        isFavourite = intent?.extras?.getString(IS_FAVOURITE).toBoolean()

        binding.changePrice.text = Stock(symbol, name, currentPrice.toDouble(),
                changePrice.toDouble(), false).getDeltaPriceString()
        binding.currentPrice.text = "$$currentPrice"
        binding.buyButton.text = "Buy for $$currentPrice"
        linechart = binding.lineChart

        title = name + "\n" + symbol
       // drawChart()

       // renderChartThread()
        runChartThread()
    }

    private fun runChartThread() {
        CoroutineScope(Dispatchers.Main).launch {
            drawChart()
        }
    }


//    private fun renderChartThread() {
//        val thread = Thread {
//            runOnUiThread { drawChart() }
//        }
//        thread.start()
//    }

    private fun setIcon(menuItem: MenuItem?) {
        if (menuItem == null)
            return

        menuItem.icon =
                if (isFavourite)
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
                val stock = Stock(symbol, name, currentPrice.toDouble(), changePrice.toDouble(), isFavourite)
                if (isFavourite) {
                    StockData.removeFromFavourites(stock)
                } else {
                    StockData.addFavouriteStock(stock)
                }
                isFavourite = !isFavourite
                setIcon(item)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun drawChart() {
        val entries = ArrayList<Entry>()
        ApiClient.apiKey["token"] = "c1esv2f48v6vvsb41tcg"
        val apiClient = DefaultApi()
        val context = this
        CoroutineScope(Dispatchers.Main).launch {

            val stockCandlesRequest = GlobalScope.async {
                apiClient.stockCandles(symbol, "D", 1585488999,
                        1617024999, null)
            }

            val stockCandles = stockCandlesRequest.await()
            val closedPrices = (stockCandles.c)
            val timestamps = stockCandles.t

//        val csvData: String = "1615299000,119.09,119.13,119.09,140.12,30712\n" +
//                "1615299060,119.12,119.12,119.08,119.08,17120\n" +
//                "1615299120,119.08,119.14,119.07,125.14,26855\n" +
//                "1615299180,119.14,119.19,119.1,230.19,32212"
            //val rows: List<List<String>> = csvReader().readAll(csvData)

            val models = ArrayList<Model>()
            //var i = 0f
            for (i in 0..closedPrices!!.size - 1) {
                models.add(Model(closedPrices[i], Date(timestamps!![i] * 1000L)))
                entries.add(Entry(i.toFloat(), closedPrices[i].toFloat()))
            }

//        for (row in closedPrices) {
//            models.add(Model(row[4].toFloat(), Date(row[0].toLong()*1000L)))
//            entries.add(Entry(i++, row[4].toFloat()))
//        }

            val dataset = LineDataSet(entries, "")
            dataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataset.setDrawFilled(true)
            dataset.setFillDrawable(ContextCompat.getDrawable(context, R.drawable.gradient));
            dataset.setDrawHighlightIndicators(true)
            //
            dataset.lineWidth = 1.95f
            dataset.circleRadius = 5f
            dataset.color = R.color.black
            dataset.setCircleColor(R.color.black)
            dataset.setDrawCircleHole(false)
            dataset.setDrawCircles(false)
            dataset.highLightColor = Color.WHITE
            dataset.setDrawValues(false)
            dataset.highLightColor = R.color.black
            dataset.highlightLineWidth = 1f
            dataset.setDrawHorizontalHighlightIndicator(false)

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
            //linechart.setDescriptionColor(Color.WHITE)
            linechart.isAutoScaleMinMaxEnabled = false

            val markerView = CustomMarkerView(context, R.layout.marker_view, models)
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
}