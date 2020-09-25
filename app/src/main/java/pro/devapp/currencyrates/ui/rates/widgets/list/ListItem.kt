package pro.devapp.currencyrates.ui.rates.widgets.list

import pro.devapp.core.entities.EntityCurrency

data class ListItem(val currency: EntityCurrency, val isSelected: Boolean = false) {
    enum class Changed {
        RATE_CHANGE,
        FULL_CHANGE
    }

    fun getChanged(item: ListItem): Changed {
        if (
            currency.code != item.currency.code ||
            currency.flag != item.currency.flag ||
            currency.name != item.currency.name ||
            isSelected != item.isSelected
        ) {
            return Changed.FULL_CHANGE
        }
        if (currency.rate != item.currency.rate) {
            return Changed.RATE_CHANGE
        }
        return Changed.FULL_CHANGE
    }
}