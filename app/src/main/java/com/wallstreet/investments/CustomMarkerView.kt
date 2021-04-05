package com.wallstreet.investments

import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.marker_view.view.*
import java.util.*

class CustomMarkerView(context: Context, layoutResource: Int, private val stockCandles: ArrayList<StockCandle>) : MarkerView(context, layoutResource) {

    override fun refreshContent(e: Entry, highlight: Highlight) {
        val xIndex = e.x
        val model = stockCandles[xIndex.toInt()]
        tvPrice.text = model.formatedPrice
        tvDate.text = model.fullDate
    }

    override fun getOffsetForDrawingAtPoint(xpos: Float, ypos: Float): MPPointF {
        return MPPointF(-width / 2f, (-height * 1.5).toFloat())
    }
}