package pro.devapp.network.api

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import pro.devapp.network.BuildConfig
import pro.devapp.network.ConnectionStateUtil
import pro.devapp.network.entities.ApiEntityCurrency
import pro.devapp.network.exceptions.InvalidApiResponseException
import pro.devapp.network.exceptions.NetworkApiException
import pro.devapp.network.exceptions.NotFoundApiException
import pro.devapp.network.exceptions.ServerApiException


class ApiCurrency(
    private val httpClient: OkHttpClient,
    private val connectionStateUtil: ConnectionStateUtil
) {
    fun getCurrencyList(currencyCode: String): List<ApiEntityCurrency> {
        val urlBuilder = BuildConfig.urlApi.toHttpUrlOrNull()?.newBuilder()
        urlBuilder?.addQueryParameter("base", currencyCode)
        val url = urlBuilder?.build().toString()
        val response = makeRequest(url)
        return handleApiResponse(response)
    }

    private fun makeRequest(url: String): Response {
        try {
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .get()
                .build()
            return httpClient.newCall(request).execute()
        } catch (e: Exception) {
            if (!connectionStateUtil.isInternetAvailable()) {
                throw NetworkApiException("You are offline")
            }
            throw e
        }
    }

    private fun handleApiResponse(response: Response): List<ApiEntityCurrency> {
        if (response.isSuccessful) {
            val json = response.body?.string()
            return json?.let {
                val currencyList = ArrayList<ApiEntityCurrency>()
                val jsonObject = JSONObject(it)
                val ratesObject = jsonObject.getJSONObject("rates")
                ratesObject.keys().forEach { currencyCode ->
                    val currencyRate = ratesObject.getDouble(currencyCode)
                    currencyList.add(
                        ApiEntityCurrency(currencyCode, currencyRate)
                    )
                }
                return currencyList
            } ?: throw InvalidApiResponseException()
        } else {
            val json = response.body?.string()
            return json?.let {
                when (response.code) {
                    in 400..450 -> {
                        throw NotFoundApiException(it)
                    }
                    else -> {
                        throw ServerApiException(it)
                    }
                }
            } ?: throw ServerApiException(response.message)
        }
    }
}