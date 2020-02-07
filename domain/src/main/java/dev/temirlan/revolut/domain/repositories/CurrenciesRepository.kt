package dev.temirlan.revolut.domain.repositories

import dev.temirlan.revolut.domain.entities.Currency

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/5/20.
 */
interface CurrenciesRepository {
    suspend fun getCurrencies(): List<Currency>
}