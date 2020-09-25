package pro.devapp.currencyrates.ui.rates.widgets.list

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import pro.devapp.currencyrates.R
import pro.devapp.currencyrates.databinding.ItemCurrencyBinding
import kotlin.math.roundToLong

class CurrencyViewHolder(itemView: View, onTextListener: ((text: String) -> Unit)?) :
    RecyclerView.ViewHolder(itemView) {
    companion object {
        const val LAYOUT_ID = R.layout.item_currency
    }

    private var isSelected = false

    private val viewBinding = ItemCurrencyBinding.bind(itemView).apply {
        currencyRate.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                root.callOnClick()
            }
        }
        currencyRate.addTextChangedListener { editable ->
            if (isSelected) {
                editable?.apply {
                    onTextListener?.let { it(toString()) }
                }
            }
        }
    }

    fun onBind(item: ListItem?) {
        item?.let {
            isSelected = it.isSelected
            viewBinding.currencyCode.text = it.currency.code
            viewBinding.flag.setImageDrawable(
                ContextCompat.getDrawable(
                    viewBinding.flag.context,
                    it.currency.flag ?: R.drawable.ic_launcher_foreground
                )
            )
            viewBinding.currencyName.text = it.currency.name
        }
        onBindRate(item)
    }

    fun onBindRate(item: ListItem?) {
        item?.let {
            val rate = it.currency.rate
            if (!it.isSelected) {
                viewBinding.currencyRate.setText(roundMoney(rate).toString())
                viewBinding.currencyRate.isEnabled = rate > 0.00
            } else {
                if (viewBinding.currencyRate.text.isNullOrEmpty()) {
                    viewBinding.currencyRate.setText(roundMoney(rate).toString())
                }
            }
        }
    }

    private fun roundMoney(amount: Double?): Double {
        return ((amount ?: 0.00) * 10000).roundToLong() / 10000.00
    }
}