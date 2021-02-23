package pro.devapp.currencyrates.usecases

import io.reactivex.Single
import pro.devapp.core.entities.EntityCurrency
import pro.devapp.storage.repositories.CurrencyRatesRepository

/**
 * Currency list
 */
class LoadRatesListUseCase(private val currencyRatesRepository: CurrencyRatesRepository) :
    BaseUseCase<Single<List<EntityCurrency>>, LoadRatesListUseCase.Params> {
    data class Params(val selectedCurrency: EntityCurrency, val currentValue: Double)

    override fun run(params: Params): Single<List<EntityCurrency>> {
        return currencyRatesRepository.getCurrencyRates(params.selectedCurrency.code)
            .map { result ->
                val fullList = result.map {
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
                fullList
            }
    }
}