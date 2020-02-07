package dev.temirlan.revolut.domain.di_modules

import dev.temirlan.revolut.domain.use_cases.CurrenciesExchangeUseCase
import dev.temirlan.revolut.domain.use_cases.CurrenciesFlowUseCase
import org.koin.dsl.module

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/5/20.
 */
fun getUseCasesModule() = module {
    factory { CurrenciesFlowUseCase(get()) }
    factory { CurrenciesExchangeUseCase() }
}