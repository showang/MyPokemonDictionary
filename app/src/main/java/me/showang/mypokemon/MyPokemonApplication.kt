package me.showang.mypokemon

import android.app.Application
import me.showang.mypokemon.di.KoinModules
import me.showang.mypokemon.repository.PokemonRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

class MyPokemonApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        startKoin {
            modules(KoinModules.modules)
        }
        get<PokemonRepository>().initData()
    }
}
