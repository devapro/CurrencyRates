package pro.devapp.network.api

import pro.devapp.network.ConnectionStateUtil
import pro.devapp.network.getClient

fun getApiCurrency(): ApiCurrency {
    return ApiCurrency(getClient(), ConnectionStateUtil())
}