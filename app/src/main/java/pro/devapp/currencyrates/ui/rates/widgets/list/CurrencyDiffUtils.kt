package pro.devapp.currencyrates.ui.rates.widgets.list

import androidx.recyclerview.widget.DiffUtil

class CurrencyDiffUtils(
    private val newItems: List<ListItem>,
    private val oldItems: List<ListItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].currency.code == newItems[newItemPosition].currency.code
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition] == newItems[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return oldItems[newItemPosition].getChanged(newItems[newItemPosition])
    }
}