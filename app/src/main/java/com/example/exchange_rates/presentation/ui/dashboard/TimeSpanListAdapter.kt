package com.example.exchange_rates.presentation.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.exchange_rates.R
import com.example.exchange_rates.domain.model.ExchangeRate
import java.text.DecimalFormat

class TimeSpanListAdapter(
    private var items: List<ExchangeRate>
) : RecyclerView.Adapter<TimeSpanListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val baseCurrency: TextView = view.findViewById(R.id.detailBaseCurrencyItem)
        val destinationCurrency: TextView = view.findViewById(R.id.detailDestinationCurrencyItem)
        val exchangeRate: TextView = view.findViewById(R.id.detailExchangeRateItem)
        val date: TextView = view.findViewById(R.id.detailDateItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_currency_with_date, parent, false)  // <-- custom layout
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currency = items[position]
        holder.baseCurrency.text = currency.baseCurrency
        holder.destinationCurrency.text = currency.destinationCurrency
        holder.exchangeRate.text = currency.exchangeRate.toString()
        holder.date.text = currency.date.toString()
    }
}
