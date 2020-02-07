package dev.temirlan.revolut.domain.use_cases

import dev.temirlan.revolut.domain.repositories.CurrenciesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/6/20.
 */
class CurrenciesFlowUseCase(
    private val currenciesRepository: CurrenciesRepository
) {

    suspend fun execute(delayInMs: Long = 1000L) = flow {
        while (true) {
            val currencies = currenciesRepository.getCurrencies()
            emit(currencies)
            delay(delayInMs)
        }
    }
}