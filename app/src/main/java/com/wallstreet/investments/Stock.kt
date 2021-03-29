package com.wallstreet.investments

import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList

class Stock (val symbol : String, val name : String,
             val currentPrice : Double, val changePrice : Double, var isFavourite : Boolean)
{
    private val models = ArrayList<Model>()

    fun addPrice(price : Float, date : Date) {
        models.add(Model(price, date))
    }

    fun getDeltaPriceString() : String {
        return "${changePrice} (${(changePrice / currentPrice * 100).toBigDecimal().setScale(3, RoundingMode.DOWN).toDouble()}%)"
    }
}