package pro.devapp.currencyrates.usecases

import io.reactivex.Single
import pro.devapp.core.entities.EntityCurrency
import pro.devapp.storage.repositories.CurrencyRatesRepository

class GetRatesListUseCase(private val currencyRatesRepository: CurrencyRatesRepository) :
    BaseUseCase<Single<List<EntityCurrency>>, GetRatesListUseCase.Params> {
    data class Params(val selectedCurrency: EntityCurrency, val currentValue: Double)

    override fun run(params: Params): Single<List<EntityCurrency>> {
        return Single.create {
            val result = currencyRatesRepository.getLastLoadedCurrencyRates()
            val fullList = result.map { item ->
                EntityCurrency(
                    item.code,
                    item.name,
                    item.flag,
                    item.rate * params.currentValue
                )
            }.toMutableList()
            fullList.reverse()
            fullList.add(params.selectedCurrency)
            fullList.reverse()
            it.onSuccess(fullList)
        }
    }
}