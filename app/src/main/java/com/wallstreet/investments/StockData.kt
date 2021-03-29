package com.wallstreet.investments

import android.widget.Adapter

class StockData {
    companion object {
        var stockList = mutableListOf<Stock>()
        var favouriteStockList = mutableListOf<Stock>()

        fun isStockListEmpty() : Boolean{
            return stockList.isEmpty()
        }
        fun isFavouriteStockListEmpty() : Boolean {
            return favouriteStockList.isEmpty()
        }
        fun getNonFavouriteStocks(): MutableList<Stock> {
            return stockList
        }
        fun getFavouriteStocks(): MutableList<Stock> {
            return favouriteStockList
        }
        fun addNonFavouriteStock(stock: Stock) {
            stockList.add(stock)
        }
        fun addFavouriteStock(stock: Stock) {
            val favStock = stock
            favStock.isFavourite = true
            favouriteStockList.add(favStock)
            changeFavouriteAttribute(stock)
        }
        fun removeFromFavourites(stock : Stock) {
            favouriteStockList.removeAll({ it.symbol == stock.symbol })
            changeFavouriteAttribute(stock)
        }
        private fun changeFavouriteAttribute(stock : Stock) {
            val foundStock = stockList.find{ it.symbol == stock.symbol }
            if (foundStock != null) {
                foundStock.isFavourite = !foundStock.isFavourite
            }
        }
    }
}