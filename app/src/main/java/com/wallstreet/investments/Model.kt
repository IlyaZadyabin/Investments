package com.wallstreet.investments

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Model(var price: Float, date: Date) {
    var date: Date? = date
    private val defaultDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val monthYearFormat = SimpleDateFormat("MMM yyyy")
    private val longFormat = SimpleDateFormat("dd MMM yyyy")
    var moneyFormatter = NumberFormat.getCurrencyInstance()

    val monthYear: String
        get() = monthYearFormat.format(this.date)

    val fullDate: String
        get() = longFormat.format(this.date)

    val formatedPrice: String
        get() = moneyFormatter.format(this.price)
}
