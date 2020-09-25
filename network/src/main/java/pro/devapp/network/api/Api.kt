package pro.devapp.network.api

import pro.devapp.network.ConnectionStateUtil
import pro.devapp.network.getClient

fun getApiCurrencyRates(): ApiCurrencyRates {
    return ApiCurrencyRates(getClient(), ConnectionStateUtil())
}