package me.showang.mypokemon

import android.app.Application
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyPokemonApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(
                module {

                }
            )
        }
    }
}
