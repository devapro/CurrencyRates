package pro.devapp.currencyrates.data

import pro.devapp.core.entities.EntityCurrency
import pro.devapp.currencyrates.ui.rates.RatesViewModel

fun getDefaultCurrencyEntity() = EntityCurrency(
    RatesViewModel.DEFAULT_CURRENCY_CODE,
    "",
    null,
    0.00
)