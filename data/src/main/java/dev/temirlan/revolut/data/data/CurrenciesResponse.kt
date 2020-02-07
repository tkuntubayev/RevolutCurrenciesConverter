package dev.temirlan.revolut.data.data

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import dev.temirlan.revolut.domain.entities.Currency
import java.util.*

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/5/20.
 */
class CurrenciesResponse {
    @SerializedName("base")
    var baseCurrencyLabel: String? = null

    @SerializedName("date")
    var date: Date? = null

    @SerializedName("rates")
    var rates: JsonObject? = null

    @Throws(NoSuchFieldException::class)
    fun toEntity(): List<Currency> {
        val currencies = ArrayList<Currency>()
        val baseCurrencyLabel = baseCurrencyLabel
            ?: throw NoSuchFieldException("Base currency cannot be null")
        val baseCurrency = Currency(baseCurrencyLabel, 1.0)
        currencies.add(baseCurrency)
        rates?.let { rates ->
            rates.keySet()?.iterator()?.forEach {
                val value = rates.get(it).asDouble
                currencies.add(Currency(it, value))
            }
        }

        return currencies
    }
}