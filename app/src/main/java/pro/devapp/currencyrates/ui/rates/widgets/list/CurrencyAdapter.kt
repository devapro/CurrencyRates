package pro.devapp.currencyrates.ui.rates.widgets.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import pro.devapp.core.entities.EntityCurrency

class CurrencyAdapter : RecyclerView.Adapter<CurrencyViewHolder>() {
    private val items = ArrayList<ListItem>()

    var onTextListener: ((text: String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(
            CurrencyViewHolder.LAYOUT_ID,
            parent,
            false
        )
        return CurrencyViewHolder(view, onTextListener)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun onBindViewHolder(
        holder: CurrencyViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            when (payloads[0]) {
                ListItem.Changed.FULL_CHANGE -> {
                    holder.onBind(items[position])
                }
                ListItem.Changed.RATE_CHANGE -> {
                    holder.onBindRate(items[position])
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(currencyList: List<ListItem>) {
        val diffResult = DiffUtil.calculateDiff(CurrencyDiffUtils(currencyList, items))
        items.clear()
        items.addAll(currencyList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItemByPosition(position: Int): EntityCurrency? {
        return if (position == RecyclerView.NO_POSITION || position >= items.size) {
            null
        } else {
            items[position].currency
        }
    }
}