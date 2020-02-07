package dev.temirlan.revolut.domain.use_cases

import dev.temirlan.revolut.domain.entities.Currency
import dev.temirlan.revolut.domain.entities.CurrencyAmount

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/5/20.
 */
class CurrenciesExchangeUseCase {

    fun execute(
        baseCurrencyAmount: CurrencyAmount,
        toCurrencies: List<Currency>
    ): List<CurrencyAmount> {
        val exchangedCurrencyAmounts = mutableListOf<CurrencyAmount>()
        toCurrencies.forEach { toCurrency ->
            val exchangedAmount = when (baseCurrencyAmount.currency.label) {
                toCurrency.label -> baseCurrencyAmount.amount
                else -> {
                    val ratioToBaseCurrency = toCurrency.weight / baseCurrencyAmount.currency.weight
                    ratioToBaseCurrency * baseCurrencyAmount.amount
                }
            }
            val exchangedCurrencyAmount = CurrencyAmount(toCurrency, exchangedAmount)
            exchangedCurrencyAmounts.add(exchangedCurrencyAmount)
        }
        return exchangedCurrencyAmounts
    }
}