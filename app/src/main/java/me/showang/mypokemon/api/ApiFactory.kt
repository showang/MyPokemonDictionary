package me.showang.mypokemon.api

import kotlinx.serialization.json.Json
import me.showang.respect.RestfulApi

class ApiFactory(private val json: Json) {
    fun createPokemonBasicInfoApi(pokemonName: String): RestfulApi<PokemonBasicInfoApi.Result> =
        PokemonBasicInfoApi(json, pokemonName)

    fun createPokemonSpecialApi(pokemonName: String): RestfulApi<PokemonSpecialApi.Result> =
        PokemonSpecialApi(json, pokemonName)

    fun createAllPokemonNameApi(): RestfulApi<List<String>> =
        AllPokemonNameApi(json)
}
