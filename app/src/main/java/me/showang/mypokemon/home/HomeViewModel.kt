package me.showang.mypokemon.home

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.showang.mypokemon.model.MyPokemon
import me.showang.mypokemon.model.PokemonInfo
import me.showang.mypokemon.repository.PokemonRepository
import me.showang.transtate.TranstateViewModel
import timber.log.Timber

class HomeViewModel(
    private val repository: PokemonRepository,
    defaultDispatcher: CoroutineDispatcher,
    initState: HomeUiState = HomeUiState.InitState(),
) : TranstateViewModel<HomeUiState>(initState, defaultDispatcher) {

    init {
        performObserveMyPocketChanges()
        performObserveTypeGroupChanges()
    }

    fun initData() = viewModelScope.launch {
        runCatching {
            val myPocketMonstersDeferred = async {
                repository.fetchMyPocketMonsters()
            }
            val typeGroupsDeferred = async {
                repository.fetchPokemonTypeGroups()
            }
            val event = HomeUiEvent.InitData(
                myPocketMonstersDeferred.await(),
                typeGroupsDeferred.await()
            )
            startTransform(event)
        }.onFailure { error ->
            Timber.e(error, "Failed to fetch data")
            startTransform(HomeUiEvent.Error(error))
        }
    }

    private fun performObserveMyPocketChanges() = viewModelScope.launch(IO) {
        repository.myPocketMonstersFlow.collect { myPocketMonsters ->
            startTransform(HomeUiEvent.UpdateMyPocket(myPocketMonsters))
        }
    }

    private fun performObserveTypeGroupChanges() = viewModelScope.launch(IO) {
        repository.pokemonTypeGroupsFlow.collect { typeGroups ->
            startTransform(HomeUiEvent.UpdateTypeGroups(typeGroups))
        }
    }

    fun saveToMyPokemon(pokemonInfo: PokemonInfo) = viewModelScope.launch(IO) {
        runCatching {
            repository.saveMyPocketMonster(pokemonInfo.name)
        }.onFailure { error ->
            Timber.e(error, "Failed to save to my pocket")
            startTransform(HomeUiEvent.Error(error))
        }
    }

    fun removeFromMyPokemon(myPokemon: MyPokemon) = viewModelScope.launch(IO) {
        runCatching {
            repository.removeMyPocketMonster(myPokemon.catchId)
        }.onFailure { error ->
            Timber.e(error, "Failed to delete from my pocket")
            startTransform(HomeUiEvent.Error(error))
        }
    }
}
