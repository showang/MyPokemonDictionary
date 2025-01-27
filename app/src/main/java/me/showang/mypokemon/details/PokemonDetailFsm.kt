package me.showang.mypokemon.details

import me.showang.mypokemon.details.ui.PokemonDetailUiModel
import me.showang.mypokemon.model.PokemonInfo
import me.showang.transtate.core.ViewEvent
import me.showang.transtate.core.ViewState

sealed class PokemonDetailUiState : ViewState<PokemonDetailUiState>() {
    data class InitState(val pokemonInfo: PokemonInfo) : PokemonDetailUiState() {
        override fun transition(byEvent: ViewEvent): PokemonDetailUiState? {
            return when (byEvent) {
                is PokemonDetailUiEvent.DataLoaded -> DataLoadedState(byEvent.uiModel)
                is PokemonDetailUiEvent.DataLoadFailed -> ErrorState(byEvent.error.message ?: "Unknown error")
                else -> null
            }
        }
    }

    data class DataLoadedState(val uiModel: PokemonDetailUiModel) : PokemonDetailUiState() {
        override fun transition(byEvent: ViewEvent): PokemonDetailUiState? {
            return null
        }
    }

    data class ErrorState(val message: String) : PokemonDetailUiState() {
        override fun transition(byEvent: ViewEvent): PokemonDetailUiState? {
            return null
        }
    }
}

sealed class PokemonDetailUiEvent : ViewEvent() {

    data class DataLoaded(val uiModel: PokemonDetailUiModel) : PokemonDetailUiEvent()
    data class DataLoadFailed(val error: Throwable) : PokemonDetailUiEvent()
}
