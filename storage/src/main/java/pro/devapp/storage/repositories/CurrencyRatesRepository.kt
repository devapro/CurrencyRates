package pro.devapp.storage.repositories

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pro.devapp.core.entities.EntityCurrency
import pro.devapp.network.api.ApiCurrencyRates
import pro.devapp.storage.mappers.map

class CurrencyRatesRepository(
    private val apiCurrencyRates: ApiCurrencyRates,
    private val currencyDetailsRepository: CurrencyDetailsRepository
) {

    fun getCurrencyRates(baseCurrencyCode: String): Single<List<EntityCurrency>> {
        return apiCurrencyRates.getCurrencyList(baseCurrencyCode)
            .subscribeOn(Schedulers.io())
            .map {
                it.map(currencyDetailsRepository)
            }
    }
}