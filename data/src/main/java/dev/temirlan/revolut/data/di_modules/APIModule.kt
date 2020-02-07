package dev.temirlan.revolut.data.di_modules

import dev.temirlan.revolut.data.network.CurrencyAPI
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/5/20.
 */
internal fun getAPIModule() = module {
    single { get<Retrofit>().create(CurrencyAPI::class.java) }
}