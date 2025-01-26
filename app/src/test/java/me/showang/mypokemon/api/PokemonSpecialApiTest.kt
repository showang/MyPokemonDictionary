package me.showang.mypokemon.api

import kotlinx.coroutines.runBlocking
import me.showang.respect.request
import kotlin.test.Test

class PokemonSpecialApiTest : BaseApiTest() {

    @Test
    fun testRequest() {
        runBlocking {
            PokemonSpecialApi(json, "fearow").request()
        }.let(::println)
    }
}
