package me.showang.mypokemon.api

import com.shopback.respect.core.HttpMethod
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.showang.respect.RestfulApi

class AllPokemonNameApi(
    private val json: Json
) : RestfulApi<List<String>>() {
    override fun parse(bytes: ByteArray): List<String> {
        return json.decodeFromString<ResponseEntity>(String(bytes)).results.map { it.name }
    }

    override val url: String = "https://pokeapi.co/api/v2/pokemon"
    override val httpMethod: HttpMethod = HttpMethod.GET
    override val urlQueries: Map<String, String> = mapOf("limit" to "151")

    @Serializable
    private data class ResponseEntity(
        val results: List<Pokemon>
    )

    @Serializable
    private data class Pokemon(
        val name: String
    )
}
