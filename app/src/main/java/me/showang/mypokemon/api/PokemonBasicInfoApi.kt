package me.showang.mypokemon.api

import com.shopback.respect.core.HttpMethod
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.showang.mypokemon.api.PokemonBasicInfoApi.Result
import me.showang.respect.RestfulApi

class PokemonBasicInfoApi(
    private val json: Json,
    private val pokemonName: String
) : RestfulApi<Result>() {
    override val httpMethod: HttpMethod = HttpMethod.GET
    override val url: String = " https://pokeapi.co/api/v2/pokemon/$pokemonName"
    override fun parse(bytes: ByteArray): Result {
        val jsonElement = json.parseToJsonElement(String(bytes))
        val imageUrl = jsonElement.jsonObject["sprites"]
            ?.jsonObject?.get("other")
            ?.jsonObject?.get("official-artwork")
            ?.jsonObject?.get("front_default")
            ?.jsonPrimitive?.contentOrNull
        val resultEntity = json.decodeFromJsonElement<ResponseEntity>(jsonElement)
        return Result(
            id = resultEntity.id,
            name = pokemonName,
            imageUrl = imageUrl ?: "",
            types = resultEntity.types.map { it.type.name }
        )
    }

    data class Result(
        val id: Long,
        val name: String,
        val imageUrl: String,
        val types: List<String>
    )

    @Serializable
    private data class ResponseEntity(
        val id: Long,
        val types: List<SlotEntity>
    )

    @Serializable
    private data class SlotEntity(
        val type: TypeEntity
    )

    @Serializable
    private data class TypeEntity(
        val name: String
    )
}
