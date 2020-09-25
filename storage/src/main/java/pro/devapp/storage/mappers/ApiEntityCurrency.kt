package pro.devapp.storage.mappers

import pro.devapp.core.entities.EntityCurrency
import pro.devapp.network.entities.ApiEntityCurrency
import pro.devapp.storage.repositories.CurrencyDetailsRepository

fun ApiEntityCurrency.map(currencyDetailsRepository: CurrencyDetailsRepository): EntityCurrency {
    val name = currencyDetailsRepository.getName(code)
    val flag = currencyDetailsRepository.getFlag(code)
    return EntityCurrency(code, name, flag, rate)
}

fun List<ApiEntityCurrency>.map(currencyDetailsRepository: CurrencyDetailsRepository): List<EntityCurrency> {
    return map { it.map(currencyDetailsRepository) }
}