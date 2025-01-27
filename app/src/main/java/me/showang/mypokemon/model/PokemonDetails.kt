package me.showang.mypokemon.model

data class PokemonDetails(
    val info: PokemonInfo,
    val types: List<String>,
    val evolutionFrom: String?,
    val description: String,
)
