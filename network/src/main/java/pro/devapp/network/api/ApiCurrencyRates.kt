package pro.devapp.network.api

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import pro.devapp.network.BuildConfig
import pro.devapp.network.ConnectionStateUtil
import pro.devapp.network.entities.ApiEntityCurrency

/**
 * Api for currency rates
 */
class ApiCurrencyRates(httpClient: OkHttpClient, connectionStateUtil: ConnectionStateUtil) :
    BaseApi(httpClient, connectionStateUtil) {
    fun getCurrencyList(currencyCode: String): List<ApiEntityCurrency> {
        val urlBuilder = BuildConfig.urlApi.toHttpUrlOrNull()?.newBuilder()
        urlBuilder?.addQueryParameter("base", currencyCode)
        val url = urlBuilder?.build().toString()
        val response = makeRequest(url)
        return handleApiResponse(response) { jsonObject ->
            val currencyList = ArrayList<ApiEntityCurrency>()
            val ratesObject = jsonObject.getJSONObject("rates")
            ratesObject.keys().forEach { currencyCode ->
                val currencyRate = ratesObject.getDouble(currencyCode)
                currencyList.add(
                    ApiEntityCurrency(currencyCode, currencyRate)
                )
            }
            currencyList
        }
    }
}