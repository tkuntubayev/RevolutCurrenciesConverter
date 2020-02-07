package dev.temirlan.revolut.domain.entities

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/5/20.
 */
data class CurrencyAmount(
    val currency: Currency,
    var amount: Double
)