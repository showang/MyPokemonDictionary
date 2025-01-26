package me.showang.mypokemon.api

import kotlinx.coroutines.runBlocking
import me.showang.respect.request
import kotlin.test.Test

class PokemonTypeApiTest : BaseApiTest() {

    @Test
    fun testRequest() {
        runBlocking {
            PokemonTypeApi(json, "fearow").request()
        }.let(::println)
    }
}
