package me.showang.mypokemon.api

import androidx.compose.ui.text.intl.Locale
import com.shopback.respect.core.HttpMethod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.showang.mypokemon.api.PokemonSpecialApi.Result
import me.showang.respect.RestfulApi

class PokemonSpecialApi(
    private val json: Json,
    private val pokemonName: String,

) : RestfulApi<Result>() {
    override val url: String = "https://pokeapi.co/api/v2/pokemon-species/$pokemonName"
    override val httpMethod: HttpMethod = HttpMethod.GET
    override fun parse(bytes: ByteArray): Result {
        val jsonString = String(bytes)
        val entity = json.decodeFromString<ResponseEntity>(jsonString)
        val language = Locale.current.language
        return Result(
            name = pokemonName,
            evolvesFrom = entity.evolvesFromEntity?.name,
            descriptions = runCatching {
                entity.flavorTextEntities.find {
                    it.language.name == language && it.version.name == "yellow"
                }?.flavorText ?: entity.flavorTextEntities.first().flavorText
            }.getOrNull() ?: "Description not found"
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
        val flavorTextEntities: List<FlavorTextEntity>,
    )

    @Serializable
    private data class FlavorTextEntity(
        @SerialName("flavor_text")
        val flavorText: String,
        val language: InfoEntity,
        val version: InfoEntity,
    )

    @Serializable
    private data class InfoEntity(
        val name: String,
        val url: String,
    )

    @Serializable
    private data class EvolvesFromEntity(
        val name: String
    )
}
