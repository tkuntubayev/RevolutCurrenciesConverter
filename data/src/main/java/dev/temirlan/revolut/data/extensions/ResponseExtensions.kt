package dev.temirlan.revolut.data.extensions

import retrofit2.Response

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/6/20.
 */

class NullResponseException : Exception()

@Throws(NullResponseException::class)
internal fun <DATA, DOMAIN> Response<DATA>.unwrap(map: (DATA) -> DOMAIN): DOMAIN {
    return when (val data = body()) {
        null -> throw NullResponseException()
        else -> map(data)
    }
}