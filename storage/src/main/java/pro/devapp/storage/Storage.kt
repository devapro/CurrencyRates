package pro.devapp.storage

import android.content.Context
import pro.devapp.network.api.getApiCurrencyRates
import pro.devapp.storage.repositories.CurrencyDetailsRepository
import pro.devapp.storage.repositories.CurrencyRatesRepository

/**
 * Get repositories
 */

object Storage {
    private var currencyRatesRepository: CurrencyRatesRepository? = null
    private var currencyDetailsRepository: CurrencyDetailsRepository? = null

    fun getCurrencyRatesRepository(context: Context): CurrencyRatesRepository {
        if (currencyRatesRepository == null) {
            currencyRatesRepository = CurrencyRatesRepository(
                getApiCurrencyRates(),
                getCurrencyDetailsRepository(context)
            )
        }
        return currencyRatesRepository!!
    }

    fun getCurrencyDetailsRepository(context: Context): CurrencyDetailsRepository {
        if (currencyDetailsRepository == null) {
            currencyDetailsRepository = CurrencyDetailsRepository(context)
        }
        return currencyDetailsRepository!!
    }
}