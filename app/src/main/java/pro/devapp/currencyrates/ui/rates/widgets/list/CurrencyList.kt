package pro.devapp.currencyrates.ui.rates.widgets.list

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.subjects.ReplaySubject
import pro.devapp.core.entities.EntityCurrency

class CurrencyList @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    val clickSubject = ReplaySubject.create<EntityCurrency>()

    private var lastSelectedCurrency: EntityCurrency? = null

    init {
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        itemAnimator = DefaultItemAnimator()
        adapter = CurrencyAdapter()
        setHasFixedSize(true)

        addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int) {
                val item = (adapter as CurrencyAdapter).getItemByPosition(position)
                item?.let {
                    clickSubject.onNext(it)
                }
            }
        })

        setItemViewCacheSize(40)
    }

    fun submitList(currencyList: List<EntityCurrency>) {
        val firstCurrencyCode = currencyList.first().code
        (adapter as CurrencyAdapter).submitList(currencyList.map {
            ListItem(
                it,
                it.code == firstCurrencyCode
            )
        })

        lastSelectedCurrency?.let {
            if (it.code != currencyList.first().code) {
                lastSelectedCurrency = currencyList.first()
                scrollToPosition(0)
            }
        } ?: run { lastSelectedCurrency = currencyList.first() }
    }

    fun setTextListener(onTextListener: ((text: String) -> Unit)) {
        (adapter as CurrencyAdapter).onTextListener = onTextListener
    }
}