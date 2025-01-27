package me.showang.mypokemon.api

import kotlinx.coroutines.runBlocking
import me.showang.respect.request
import kotlin.test.Test

class PokemonBasicInfoApiTest : BaseApiTest() {

    @Test
    fun testRequest() {
        runBlocking {
            PokemonBasicInfoApi(json, "fearow").request()
        }.let(::println)
    }
}
