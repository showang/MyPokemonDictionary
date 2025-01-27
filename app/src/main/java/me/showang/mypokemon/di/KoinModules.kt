package me.showang.mypokemon.di

import com.shopback.respect.core.RequestExecutor
import com.shopback.respect.okhttp.OkhttpRequestExecutor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import me.showang.mypokemon.api.ApiFactory
import me.showang.mypokemon.home.HomeViewModel
import me.showang.mypokemon.navigator.MyPokemonNavigator
import me.showang.mypokemon.navigator.MyPokemonNavigatorImpl
import me.showang.mypokemon.repository.PokemonRepository
import me.showang.mypokemon.repository.PokemonRepositoryImpl
import me.showang.transtate.async.AsyncAndroid
import me.showang.transtate.async.AsyncDelegate
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object KoinModules {

    private val repository = module {
        single<PokemonRepository> { PokemonRepositoryImpl(get(), get()) }
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

    private val network = module {
        single { OkHttpClient() }
        single<RequestExecutor> { OkhttpRequestExecutor(get()) }
        single {
            Json {
                ignoreUnknownKeys = true
                isLenient = true
                explicitNulls = false
            }
        }
        single { ApiFactory(get()) }
    }

    val modules: List<Module> = listOf(viewModel, repository, coroutines, navigator, network)
}
