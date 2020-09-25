package pro.devapp.currencyrates.usecases

import pro.devapp.core.entities.EntityCurrency
import pro.devapp.storage.repositories.CurrencyDetailsRepository

class GetCurrencyByCodeUseCase(private val currencyDetailsRepository: CurrencyDetailsRepository) :
    BaseUseCase<EntityCurrency, GetCurrencyByCodeUseCase.Params> {
    data class Params(val currencyCode: String, val currentValue: Double)

    override suspend fun run(params: Params): EntityCurrency {
        val selectedCurrencyName = currencyDetailsRepository.getName(params.currencyCode)
        val selectedCurrencyFlag = currencyDetailsRepository.getFlag(params.currencyCode)
        return EntityCurrency(
            params.currencyCode,
            selectedCurrencyName,
            selectedCurrencyFlag,
            params.currentValue
        )
    }
}