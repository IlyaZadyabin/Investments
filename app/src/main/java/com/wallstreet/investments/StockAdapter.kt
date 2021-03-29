package com.wallstreet.investments

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception
import java.math.RoundingMode
import java.util.*
import kotlin.math.sign


class StockAdapter(list : MutableList<Stock>) :
    RecyclerView.Adapter<StockAdapter.StockViewHolder>(), Filterable {

    var isFavouriteStocks = false
    var stockList = list.toMutableList()
    var stockFilterList = mutableListOf<Stock>()

    init {
        stockFilterList = stockList
    }

    fun updateStockList(newList : MutableList<Stock>) {
        isFavouriteStocks = !isFavouriteStocks
        stockList.clear()
        stockList.addAll(newList)
        this.notifyDataSetChanged()
    }

    class StockViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val card = view.findViewById<CardView>(R.id.button_item)
        val symbolView = view.findViewById<TextView>(R.id.symbol)
        val nameView = view.findViewById<TextView>(R.id.name)
        val currentPriceView = view.findViewById<TextView>(R.id.currentPrice)
        val changePriceView = view.findViewById<TextView>(R.id.changePrice)
        val favouriteImageView = view.findViewById<ImageView>(R.id.favouriteImageView)
    }

    override fun getItemCount(): Int {
        return stockFilterList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        var layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.stock_view, parent, false)
        try {
            layout = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.test_stock_view, parent, false)
        } catch (e : Exception) {
            Toast.makeText(parent.context, "Inflate error: " + e.message.toString(), Toast.LENGTH_SHORT).show()
        }
        return StockViewHolder(layout)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    stockFilterList = stockList
                } else {
                    val resultList = mutableListOf<Stock>()
                    for (row in stockList) {
                        if (row.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT)) ||
                                row.symbol.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    stockFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = stockFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                stockFilterList = results?.values as MutableList<Stock>
                notifyDataSetChanged()
            }
        }
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val item = stockFilterList[position]

        if (item.isFavourite)
            holder.favouriteImageView.setImageResource(R.drawable.ic_favourite)
        else
            holder.favouriteImageView.setImageResource(R.drawable.ic_non_favourite_black)

        holder.symbolView.text = item.symbol
        holder.nameView.text = item.name
        holder.currentPriceView.text = "$${item.currentPrice}"
        //holder.changePriceView.text = "${item.changePrice} (${(item.changePrice / item.currentPrice * 100).toBigDecimal().setScale(3, RoundingMode.DOWN).toDouble()}%)"
        holder.changePriceView.text = item.getDeltaPriceString()
        holder.card.setOnClickListener{
            val context = holder.view.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.NAME, item.name)
            intent.putExtra(DetailActivity.SYMBOL, item.symbol)
            intent.putExtra(DetailActivity.CHANGE_PRICE, item.changePrice.toString())
            intent.putExtra(DetailActivity.CURRENT_PRICE, item.currentPrice.toString())
            intent.putExtra(DetailActivity.IS_FAVOURITE, item.isFavourite.toString())
            context.startActivity(intent)
        }
    }
}