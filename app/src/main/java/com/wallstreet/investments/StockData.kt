package com.wallstreet.investments

import android.widget.Adapter

class StockData {
    companion object {
        var allStocks = mutableMapOf<String, Stock>()
        var favouriteStocks = mutableMapOf<String, Stock>()

        fun isAllStocksEmpty() : Boolean {
            return allStocks.isEmpty()
        }
        fun isFavouriteStocksEmpty() : Boolean {
            return favouriteStocks.isEmpty()
        }
        fun addStock(stock: Stock) {
            allStocks[stock.symbol] = stock
        }
        fun addFavouriteStock(stock: Stock) {
            changeFavouriteAttribute(stock)
            stock.isFavourite = true
            favouriteStocks[stock.symbol] = stock
        }
        fun removeFromFavourites(stock : Stock) {
            favouriteStocks.remove(stock.symbol)
            changeFavouriteAttribute(stock)
        }
        private fun changeFavouriteAttribute(stock : Stock) {
            allStocks[stock.symbol]?.isFavourite = !(allStocks[stock.symbol]?.isFavourite)!!
        }
    }
}