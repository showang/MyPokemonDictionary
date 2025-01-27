package me.showang.mypokemon.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shopback.respect.core.RequestExecutor
import com.shopback.respect.okhttp.OkhttpRequestExecutor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import me.showang.mypokemon.api.ApiFactory
import me.showang.mypokemon.db.MyPokemonDatabase
import me.showang.mypokemon.db.MyPokemonDatabase.Companion.DATABASE_NAME
import me.showang.mypokemon.home.HomeViewModel
import me.showang.mypokemon.navigator.MyPokemonNavigator
import me.showang.mypokemon.navigator.MyPokemonNavigatorImpl
import me.showang.mypokemon.repository.PokemonRepository
import me.showang.mypokemon.repository.PokemonRepositoryImpl
import me.showang.transtate.async.AsyncAndroid
import me.showang.transtate.async.AsyncDelegate
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.reflect.KClass

object KoinModules {

    private val repository = module {
        single<PokemonRepository> { PokemonRepositoryImpl(get(), get(), get()) }
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

    private val database = module {
        single { MyPokemonDatabase::class.create(androidContext(), DATABASE_NAME) }
    }

    private fun <T : RoomDatabase, Clazz : KClass<T>> Clazz.create(context: Context, name: String) =
        Room.databaseBuilder(context, this.java, name).build()

    val modules: List<Module> = listOf(viewModel, repository, coroutines, navigator, network, database)
}
