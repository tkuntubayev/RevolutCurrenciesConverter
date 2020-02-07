package dev.temirlan.revolut.data.network

import dev.temirlan.revolut.data.data.CurrenciesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/5/20.
 */
interface CurrencyAPI {
    @GET("latest")
    suspend fun getCurrencies(
        @Query("base") baseCurrencyLabel: String
    ): Response<CurrenciesResponse>
}