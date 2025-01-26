package me.showang.mypokemon.api

import kotlinx.serialization.json.Json

abstract class BaseApiTest {

    protected val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }
}