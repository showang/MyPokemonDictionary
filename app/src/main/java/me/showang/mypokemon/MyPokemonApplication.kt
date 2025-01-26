package me.showang.mypokemon

import android.app.Application
import me.showang.mypokemon.di.KoinModules
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

class MyPokemonApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        startKoin {
            modules(KoinModules.modules)
        }
    }
}
