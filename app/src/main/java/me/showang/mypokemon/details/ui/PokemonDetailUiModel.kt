package me.showang.mypokemon.details.ui

import me.showang.mypokemon.model.PokemonDetails
import me.showang.mypokemon.model.PokemonInfo

data class PokemonDetailUiModel(
    val details: PokemonDetails,
    val evolutionInfo: PokemonInfo?
)
