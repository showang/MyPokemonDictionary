package me.showang.mypokemon.home

import me.showang.mypokemon.model.MyPokemon
import me.showang.mypokemon.model.PokemonTypeGroup
import me.showang.transtate.core.ViewEvent
import me.showang.transtate.core.ViewState

sealed class HomeUiState : ViewState<HomeUiState>() {

    // For launching the page, shows loading or initializing layout.
    class InitState : HomeUiState() {
        override fun transition(byEvent: ViewEvent): HomeUiState? {
            return when (byEvent) {
                is HomeUiEvent.InitData -> byEvent.nextState()
                is HomeUiEvent.Error -> {
                    val errorMessage = "Failed to fetch data: ${byEvent.error.message}"
                    ErrorState(errorMessage)
                }
                else -> null
            }
        }
    }

    // Initialized completed state, shows the main content of the page.
    data class LaunchedState(
        val myPokemons: List<MyPokemon>,
        val typeCategories: List<PokemonTypeGroup>
    ) : HomeUiState() {
        override fun transition(byEvent: ViewEvent): HomeUiState? {
            return when (byEvent) {
                is HomeUiEvent.UpdateTypeGroups -> copy(typeCategories = byEvent.typeGroups)
                is HomeUiEvent.UpdateMyPocket -> copy(myPokemons = byEvent.myPokemons)
                else -> this
            }
        }
    }

    // Error state, shows error message.
    data class ErrorState(val message: String) : HomeUiState() {
        override fun transition(byEvent: ViewEvent): HomeUiState? {
            return when (byEvent) {
                is HomeUiEvent.InitData -> byEvent.nextState()
                else -> null
            }
        }
    }

    protected fun HomeUiEvent.InitData.nextState() =
        LaunchedState(myPokemons, typeCategories)
}

sealed class HomeUiEvent : ViewEvent() {
    data class InitData(
        val myPokemons: List<MyPokemon>,
        val typeCategories: List<PokemonTypeGroup>
    ) : HomeUiEvent()

    data class UpdateTypeGroups(val typeGroups: List<PokemonTypeGroup>) : HomeUiEvent()
    data class UpdateMyPocket(val myPokemons: List<MyPokemon>) : HomeUiEvent()

    data class Error(val error: Throwable) : HomeUiEvent()
}
