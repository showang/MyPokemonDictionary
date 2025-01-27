package me.showang.mypokemon.api

import com.shopback.respect.core.HttpMethod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.showang.mypokemon.api.PokemonSpecialApi.Result
import me.showang.respect.RestfulApi

class PokemonSpecialApi(
    private val json: Json,
    private val pokemonName: String
) : RestfulApi<Result>() {
    override val url: String = "https://pokeapi.co/api/v2/pokemon-species/$pokemonName"
    override val httpMethod: HttpMethod = HttpMethod.GET
    override fun parse(bytes: ByteArray): Result {
        val jsonString = String(bytes)
        val entity = json.decodeFromString<ResponseEntity>(jsonString)
        return Result(
            name = pokemonName,
            evolvesFrom = entity.evolvesFromEntity?.name,
            descriptions = entity.flavorTextJsonArray.firstOrNull()?.jsonObject?.get("flavor_text")?.jsonPrimitive?.content ?: ""
        )
    }

    data class Result(
        val name: String,
        val evolvesFrom: String?,
        val descriptions: String
    )

    @Serializable
    private data class ResponseEntity(
        @SerialName("evolves_from_species")
        val evolvesFromEntity: EvolvesFromEntity?,
        @SerialName("flavor_text_entries")
        val flavorTextJsonArray: JsonArray
    )

    @Serializable
    private data class EvolvesFromEntity(
        val name: String
    )
}
