package pro.devapp.storage.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.devapp.core.entities.EntityCurrency
import pro.devapp.core.entities.ResultEntity
import pro.devapp.network.api.ApiCurrency
import pro.devapp.network.exceptions.NetworkApiException
import pro.devapp.storage.mappers.map

class CurrencyRatesRepository(
    private val apiCurrency: ApiCurrency,
    private val currencyDetailsRepository: CurrencyDetailsRepository
) {

    suspend fun getCurrencyRates(baseCurrencyCode: String): ResultEntity<List<EntityCurrency>> {
        return withContext(Dispatchers.IO) {
            try {
                val result = apiCurrency.getCurrencyList(baseCurrencyCode)
                ResultEntity.Success(result.map(currencyDetailsRepository))
            } catch (e: Exception) {
                val exception = NetworkApiException(e.message)
                ResultEntity.Error(exception)
            }
        }
    }
}