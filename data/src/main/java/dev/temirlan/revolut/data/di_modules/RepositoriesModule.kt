package dev.temirlan.revolut.data.di_modules

import dev.temirlan.revolut.data.repositories.BaseCurrenciesRepository
import dev.temirlan.revolut.domain.repositories.CurrenciesRepository
import org.koin.dsl.module

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/5/20.
 */
fun getRepositoriesModule() = module {
    single<CurrenciesRepository> { BaseCurrenciesRepository() }
}