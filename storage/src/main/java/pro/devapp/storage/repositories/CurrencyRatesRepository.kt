package pro.devapp.storage.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.devapp.core.entities.EntityCurrency
import pro.devapp.network.api.ApiCurrency
import pro.devapp.network.exceptions.NetworkApiException
import pro.devapp.storage.mappers.map

class CurrencyRatesRepository(
    private val apiCurrency: ApiCurrency,
    private val currencyDetailsRepository: CurrencyDetailsRepository
) {

    suspend fun getCurrencyRates(baseCurrencyCode: String): Result<List<EntityCurrency>> {
        return withContext(Dispatchers.IO) {
            val result = apiCurrency.getCurrencyList(baseCurrencyCode)
            return@withContext result.getOrNull()?.run {
                Result.success(map(currencyDetailsRepository))
            } ?: run {
                val exception = NetworkApiException(result.exceptionOrNull()?.message)
                Result.failure<List<EntityCurrency>>(exception)
            }
        }
    }
}