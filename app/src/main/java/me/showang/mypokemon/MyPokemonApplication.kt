package me.showang.mypokemon

import android.app.Application
import me.showang.mypokemon.di.KoinModules
import me.showang.mypokemon.repository.PokemonRepository
import org.koin.android.ext.koin.androidContext
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
            androidContext(this@MyPokemonApplication)
            modules(KoinModules.modules)
        }
        runCatching {
            get<PokemonRepository>().initData()
        }.onFailure {
            Timber.e(it, "initData failed")
        }
    }
}
