package me.showang.mypokemon.api

import kotlinx.serialization.json.Json

class ApiFactory(private val json: Json) {
    fun createPokemonTypeApi(pokemonName: String) =
        PokemonTypeApi(json, pokemonName)

    fun createPokemonSpecialApi(pokemonName: String) =
        PokemonSpecialApi(json, pokemonName)

    fun createAllPokemonNameApi() =
        AllPokemonNameApi(json)
}
