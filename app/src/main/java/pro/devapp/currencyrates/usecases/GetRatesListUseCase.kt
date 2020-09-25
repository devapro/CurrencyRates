package pro.devapp.currencyrates.usecases

import pro.devapp.core.entities.EntityCurrency
import pro.devapp.core.entities.ResultEntity
import pro.devapp.storage.repositories.CurrencyRatesRepository

class GetRatesListUseCase(private val currencyRatesRepository: CurrencyRatesRepository) :
    BaseUseCase<ResultEntity<List<EntityCurrency>>, GetRatesListUseCase.Params> {
    data class Params(val selectedCurrency: EntityCurrency, val currentValue: Double)

    override suspend fun run(params: Params): ResultEntity<List<EntityCurrency>> {
        return when (val result =
            currencyRatesRepository.getCurrencyRates(params.selectedCurrency.code)) {
            is ResultEntity.Success -> {
                val fullList = result.value.map {
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
                ResultEntity.Success(fullList)
            }
            is ResultEntity.Error -> {
                ResultEntity.Error(result.cause)
            }
        }
    }
}