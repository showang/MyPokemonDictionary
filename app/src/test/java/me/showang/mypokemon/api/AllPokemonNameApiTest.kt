package me.showang.mypokemon.api

import kotlinx.coroutines.runBlocking
import me.showang.respect.request
import kotlin.test.Test

class AllPokemonNameApiTest : BaseApiTest() {

    @Test
    fun testRequest() {
        val result = runBlocking {
            AllPokemonNameApi(json).request()
        }
        println(result)
    }
}
