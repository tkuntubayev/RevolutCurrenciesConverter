package dev.temirlan.revolut.data

import dev.temirlan.revolut.data.di_modules.getAPIModule
import dev.temirlan.revolut.data.di_modules.getNetworkModule
import org.koin.core.Koin
import org.koin.dsl.koinApplication

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/5/20.
 */
class DataLayer {

    companion object {
        internal lateinit var koin: Koin

        fun init() {
            val dataLayerModules = listOf(
                getNetworkModule(),
                getAPIModule()
            )
            koin = koinApplication { modules(dataLayerModules) }.koin
        }
    }
}