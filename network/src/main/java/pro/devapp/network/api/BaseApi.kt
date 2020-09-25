package pro.devapp.network.api

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import pro.devapp.network.ConnectionStateUtil
import pro.devapp.network.exceptions.InvalidApiResponseException
import pro.devapp.network.exceptions.NetworkApiException
import pro.devapp.network.exceptions.NotFoundApiException
import pro.devapp.network.exceptions.ServerApiException

/**
 * Class with common methods for making api requests
 */
open class BaseApi(
    private val httpClient: OkHttpClient,
    private val connectionStateUtil: ConnectionStateUtil
) {
    protected fun makeRequest(url: String): Response {
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

    protected fun <T> handleApiResponse(
        response: Response,
        resultParser: (jsonObject: JSONObject) -> T
    ): T {
        if (response.isSuccessful) {
            val json = response.body?.string()
            return json?.let {
                val jsonObject = JSONObject(it)
                return resultParser(jsonObject)
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