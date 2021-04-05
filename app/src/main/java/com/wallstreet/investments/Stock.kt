package com.wallstreet.investments

import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class Stock (val symbol : String, val name : String,
             val currentPrice : Double, val changePrice : Double, var isFavourite : Boolean)
{
    var stockCandles = ArrayList<StockCandle>()

    fun addPrice(price : Float, date : Date) {
        stockCandles.add(StockCandle(price, date))
    }

    fun getDeltaPriceString() : String {
        val baseStr =  "$${changePrice.absoluteValue} " +
                "(${(changePrice.absoluteValue / currentPrice.absoluteValue * 100)
                        .toBigDecimal().setScale(3, RoundingMode.DOWN).toDouble()}%)"

        return if (changePrice < 0) {
            "-$baseStr"
        } else {
            "+$baseStr"
        }
    }
}