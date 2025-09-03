package com.example.exchange_rates.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.exchange_rates.R

class HomeListAdapter(
    private var items: List<String>,
    private val favourites: MutableMap<String, Boolean>,
    private val onFavouriteToggle: (String) -> Unit,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<HomeListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.currency_text)
        val starButton: ImageButton = view.findViewById(R.id.star_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_currency_with_star, parent, false)  // <-- custom layout
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currency = items[position]
        holder.textView.text = currency

        val isFavourite = favourites[currency] ?: false
        val starIconRes = if (isFavourite) {
            R.drawable.ic_home_black_24dp  // filled star drawable
        } else {
            R.drawable.ic_launcher_background  // outline star drawable
        }
        holder.starButton.setImageResource(starIconRes)

        holder.starButton.setOnClickListener {
            onFavouriteToggle(currency)
        }
        holder.itemView.setOnClickListener {
            onItemClick(currency)
        }
    }

    fun updateItems(tabIndex: Int) {
        // 0 true items (favourites) 1 false items (not favourites)
        when (tabIndex) {
            0 -> items = favourites.filter{ it.value }.keys.toList()
            1 -> items = favourites.filter{ !it.value }.keys.toList()
        }
        notifyDataSetChanged()
    }
}
