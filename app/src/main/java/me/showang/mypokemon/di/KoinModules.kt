package me.showang.mypokemon.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import me.showang.mypokemon.home.HomeViewModel
import me.showang.mypokemon.navigator.MyPokemonNavigator
import me.showang.mypokemon.navigator.MyPokemonNavigatorImpl
import me.showang.mypokemon.repository.PokemonRepository
import me.showang.mypokemon.repository.PokemonRepositoryImpl
import me.showang.transtate.async.AsyncAndroid
import me.showang.transtate.async.AsyncDelegate
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object KoinModules {

    private val repository = module {
        singleOf<PokemonRepository>(::PokemonRepositoryImpl)
    }

    private val viewModel = module {
        single { HomeViewModel(get(), get()) }
    }

    private val coroutines = module {
        single<CoroutineDispatcher> { Dispatchers.Main }
        single<AsyncDelegate> { AsyncAndroid() }
    }

    private val navigator = module {
        single<MyPokemonNavigator> { MyPokemonNavigatorImpl() }
    }

    val modules: List<Module> = listOf(viewModel, repository, coroutines, navigator)
}
