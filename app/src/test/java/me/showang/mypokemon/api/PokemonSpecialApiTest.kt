package me.showang.mypokemon.api

import kotlinx.coroutines.runBlocking
import me.showang.respect.request
import kotlin.test.Test

class PokemonSpecialApiTest : BaseApiTest() {

    @Test
    fun testRequest() {
//        val names = runBlocking {
//            AllPokemonNameApi(json).request()
//        }
//        names.forEach { name ->
            runBlocking {
//                println("Requesting $name")
                PokemonSpecialApi(json, "bulbasaur").request()
            }.let {
                println("bulbasaur: $it")
            }
//        }
    }
}
