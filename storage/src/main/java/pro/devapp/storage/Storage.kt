package pro.devapp.storage

import android.content.Context
import pro.devapp.network.api.getApiCurrency
import pro.devapp.storage.repositories.CurrencyDetailsRepository
import pro.devapp.storage.repositories.CurrencyRatesRepository

fun getCurrencyRatesRepository(context: Context): CurrencyRatesRepository {
    return CurrencyRatesRepository(getApiCurrency(), getCurrencyDetailsRepository(context))
}

fun getCurrencyDetailsRepository(context: Context): CurrencyDetailsRepository {
    return CurrencyDetailsRepository(context)
}