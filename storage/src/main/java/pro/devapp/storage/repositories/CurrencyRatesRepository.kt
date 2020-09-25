package pro.devapp.storage.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.devapp.core.entities.EntityCurrency
import pro.devapp.core.entities.ResultEntity
import pro.devapp.network.api.ApiCurrencyRates
import pro.devapp.network.exceptions.NetworkApiException
import pro.devapp.storage.mappers.map

class CurrencyRatesRepository(
    private val apiCurrencyRates: ApiCurrencyRates,
    private val currencyDetailsRepository: CurrencyDetailsRepository
) {

    suspend fun getCurrencyRates(baseCurrencyCode: String): ResultEntity<List<EntityCurrency>> {
        return withContext(Dispatchers.IO) {
            try {
                val result = apiCurrencyRates.getCurrencyList(baseCurrencyCode)
                ResultEntity.Success(result.map(currencyDetailsRepository))
            } catch (e: Exception) {
                val exception = NetworkApiException(e.message)
                ResultEntity.Error(exception)
            }
        }
    }
}