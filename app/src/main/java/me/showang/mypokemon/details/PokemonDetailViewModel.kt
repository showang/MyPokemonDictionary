package me.showang.mypokemon.details

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import me.showang.mypokemon.details.ui.PokemonDetailUiModel
import me.showang.mypokemon.model.PokemonInfo
import me.showang.mypokemon.repository.PokemonRepository
import me.showang.transtate.TranstateViewModel
import timber.log.Timber

class PokemonDetailViewModel(
    private val pokemonInfo: PokemonInfo,
    private val repository: PokemonRepository,
    defaultCoroutineDispatcher: CoroutineDispatcher,
    initState: PokemonDetailUiState = PokemonDetailUiState.InitState(pokemonInfo),
) : TranstateViewModel<PokemonDetailUiState>(initState, defaultCoroutineDispatcher) {

    fun init() = viewModelScope.launch {
        runCatching {
            val details = repository.fetchPokemonDetails(pokemonInfo.name)
            val evolutionInfo = details?.evolutionFrom?.let { name ->
                repository.fetchPokemonDetails(name)?.info
            }
            PokemonDetailUiModel(details!!, evolutionInfo)
        }.onFailure { error ->
            Timber.e(error, "fetchPokemonDetails failed")
        }.getOrNull()?.let { uiModel ->
            startTransform(PokemonDetailUiEvent.DataLoaded(uiModel))
        }
    }
}
