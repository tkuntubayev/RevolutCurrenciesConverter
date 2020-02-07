package dev.temirlan.revolut.domain.use_cases

import dev.temirlan.revolut.domain.entities.Currency
import dev.temirlan.revolut.domain.entities.CurrencyAmount
import org.junit.Before
import org.junit.Test

/**
 * Created by Temirlan Kuntubayev <t.me></t.me>/tkuntubaev> on 2/5/20.
 */
class CurrenciesExchangeUseCaseTest {

    private val useCase = CurrenciesExchangeUseCase()
    private lateinit var baseCurrencyAmount: CurrencyAmount

    @Before
    fun setUp() {
        baseCurrencyAmount = CurrencyAmount(
            Currency("KZT", 1.0),
            50000.0
        )
    }

    @Test
    fun toOtherCurrencyExchangeSuccess() {
        val currencies = listOf(
            Currency("USD", 0.002631)
        )
        val result = useCase.execute(baseCurrencyAmount, currencies)
        assert(result.getOrNull(0)?.amount == 131.55)
    }

    @Test(expected = AssertionError::class)
    fun toBaseCurrencyExchangeSuccess() {
        val currencies = listOf(
            Currency("KZT", 1.0)
        )
        val result = useCase.execute(baseCurrencyAmount, currencies)
        assert(result.getOrNull(0)?.amount != 50000.0)
    }
}