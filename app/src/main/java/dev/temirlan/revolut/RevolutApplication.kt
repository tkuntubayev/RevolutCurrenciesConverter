package dev.temirlan.revolut

import android.app.Application
import com.blongho.country_data.World
import dev.temirlan.revolut.data.DataLayer
import dev.temirlan.revolut.data.di_modules.getRepositoriesModule
import dev.temirlan.revolut.domain.di_modules.getUseCasesModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/6/20.
 */
class RevolutApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DataLayer.init()
        World.init(this)

        startKoin {
            androidContext(this@RevolutApplication)
            val koinModules = listOf(
                getRepositoriesModule(),
                getUseCasesModule()
            )
            modules(koinModules)
        }
    }
}