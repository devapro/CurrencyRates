package pro.devapp.currencyrates.usecases

import pro.devapp.core.entities.EntityCurrency
import pro.devapp.storage.repositories.CurrencyRatesRepository

class GetRatesListUseCase(private val currencyRatesRepository: CurrencyRatesRepository) :
    BaseUseCase<Result<List<EntityCurrency>>, GetRatesListUseCase.Params> {
    data class Params(val selectedCurrency: EntityCurrency, val currentValue: Double)

    override suspend fun run(params: Params): Result<List<EntityCurrency>> {
        val result = currencyRatesRepository.getCurrencyRates(params.selectedCurrency.code)
        return result.getOrNull()?.let { currencyRates ->
            val fullList = currencyRates.map {
                EntityCurrency(
                    it.code,
                    it.name,
                    it.flag,
                    it.rate * params.currentValue
                )
            }.toMutableList()
            fullList.reverse()
            fullList.add(params.selectedCurrency)
            fullList.reverse()
            Result.success(fullList)
        } ?: result
    }
}