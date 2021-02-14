package pro.devapp.network.api

import io.reactivex.Single
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import pro.devapp.network.BuildConfig
import pro.devapp.network.ConnectionStateUtil
import pro.devapp.network.entities.ApiEntityCurrency
import java.io.InterruptedIOException

/**
 * Api for currency rates
 */
class ApiCurrencyRates(httpClient: OkHttpClient, connectionStateUtil: ConnectionStateUtil) :
    BaseApi(httpClient, connectionStateUtil) {
    fun getCurrencyList(currencyCode: String): Single<List<ApiEntityCurrency>> {
        return Single.create { emitter ->
            val urlBuilder = BuildConfig.urlApi.toHttpUrlOrNull()?.newBuilder()
            urlBuilder?.addQueryParameter("base", currencyCode)
            val url = urlBuilder?.build().toString()
            try {
                val response = makeRequest(url)
                val currencyList = handleApiResponse(response) { jsonObject ->
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
                emitter.onSuccess(currencyList)
            } catch (e: Exception) {
                if (e !is InterruptedIOException) {
                    emitter.onError(e)
                }
            }
        }
    }
}