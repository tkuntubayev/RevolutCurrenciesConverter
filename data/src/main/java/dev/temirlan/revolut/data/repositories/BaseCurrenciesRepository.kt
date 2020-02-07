package dev.temirlan.revolut.data.repositories

import dev.temirlan.revolut.data.DataLayer
import dev.temirlan.revolut.data.data.CurrenciesResponse
import dev.temirlan.revolut.data.extensions.unwrap
import dev.temirlan.revolut.data.network.CurrencyAPI
import dev.temirlan.revolut.domain.entities.Currency
import dev.temirlan.revolut.domain.repositories.CurrenciesRepository

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/5/20.
 */
class BaseCurrenciesRepository : CurrenciesRepository {

    private val currencyApi: CurrencyAPI by DataLayer.koin.inject()

    override suspend fun getCurrencies(): List<Currency> {
        val response = currencyApi.getCurrencies("EUR")
        return response.unwrap(CurrenciesResponse::toEntity)
    }
}