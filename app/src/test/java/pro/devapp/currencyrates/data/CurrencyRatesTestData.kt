package pro.devapp.currencyrates.data

import pro.devapp.core.entities.EntityCurrency
import pro.devapp.currencyrates.usecases.CreateCurrencyByCodeUseCase
import pro.devapp.currencyrates.usecases.GetRatesListUseCase
import pro.devapp.currencyrates.usecases.LoadRatesListUseCase

fun getCreateCurrencyParams() = CreateCurrencyByCodeUseCase.Params(
    "EUR",
    1.00
)

fun getDefaultCurrencyEntity(value: Double = 1.00) = EntityCurrency(
    "EUR",
    "",
    null,
    value
)

fun getLoadRatesListUseCaseParams(value: Double = 1.00) = LoadRatesListUseCase.Params(
    getDefaultCurrencyEntity(value),
    value
)

fun getCurrencyRatesList() = listOf(getDefaultCurrencyEntity(1.00))

fun getListUseCaseParams() = GetRatesListUseCase.Params(
    getDefaultCurrencyEntity(2.00),
    2.00
)