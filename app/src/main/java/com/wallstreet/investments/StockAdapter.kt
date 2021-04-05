package com.wallstreet.investments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class StockAdapter(stocks_ : MutableMap<String, Stock>) :
    RecyclerView.Adapter<StockAdapter.StockViewHolder>(), Filterable {

    var isFavouriteStocks = false
    var stocks = stocks_.map{ it.value }.toMutableList()
    var filteredStocks = mutableListOf<Stock>()

    init {
        filteredStocks = stocks
    }

    fun updateStockList(newStocks : MutableMap<String, Stock>) {
        isFavouriteStocks = !isFavouriteStocks
        stocks.clear()
        stocks.addAll(newStocks.map{ it.value })
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
        return filteredStocks.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.stock_view, parent, false)
        return StockViewHolder(layout)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filteredStocks = stocks
                } else {
                    val resultList = mutableListOf<Stock>()
                    for (stock in stocks) {
                        if (stock.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT)) ||
                                stock.symbol.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(stock)
                        }
                    }
                    filteredStocks = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredStocks
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredStocks = results?.values as MutableList<Stock>
                notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val item = filteredStocks[position]

        if (item.isFavourite)
            holder.favouriteImageView.setImageResource(R.drawable.ic_favourite)
        else
            holder.favouriteImageView.setImageResource(R.drawable.ic_non_favourite_black)

        holder.symbolView.text = item.symbol
        holder.nameView.text = item.name
        holder.currentPriceView.text = "$${item.currentPrice}"
        holder.changePriceView.text = "${item.getDeltaPriceString()}"
        if (item.getDeltaPriceString().first() == '-') {
            holder.changePriceView.setTextAppearance(R.style.day_delta_minus)
        }
        holder.card.setOnClickListener{
            val context = holder.view.context
            val intent = Intent(context, StockActivity::class.java)
            intent.putExtra(StockActivity.SYMBOL, item.symbol)
            context.startActivity(intent)
        }
    }
}