package me.showang.mypokemon.model

data class PocketMonDetails(
    val info: PokemonInfo,
    val types: List<PocketMonType>,
    val evolutionFrom: PokemonInfo?,
    val descriptions: String,
)
