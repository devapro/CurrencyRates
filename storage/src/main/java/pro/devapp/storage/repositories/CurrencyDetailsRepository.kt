package pro.devapp.storage.repositories

import android.content.Context
import com.blongho.country_data.World
import pro.devapp.storage.R

class CurrencyDetailsRepository(context: Context) {

    init {
        World.init(context)
    }

    fun getFlag(currencyCode: String): Int? {
        return when (currencyCode) {
            "USD" -> R.drawable.ic_usd
            "EUR" -> R.drawable.ic_eur
            else -> {
                val country = World.getAllCurrencies().firstOrNull {
                    it.code.toUpperCase() == currencyCode.toUpperCase()
                }?.country
                country?.run { World.getFlagOf(this) }
            }
        }
    }

    fun getName(currencyCode: String): String? {
        return World.getAllCurrencies().firstOrNull {
            it.code.toUpperCase() == currencyCode.toUpperCase()
        }?.name
    }
}