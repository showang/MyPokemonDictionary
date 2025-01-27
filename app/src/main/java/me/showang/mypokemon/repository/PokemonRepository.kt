package me.showang.mypokemon.repository

import kotlinx.coroutines.flow.StateFlow
import me.showang.mypokemon.model.MyPokemon
import me.showang.mypokemon.model.PokemonTypeGroup

interface PokemonRepository {

    fun initData()

    val myPocketMonstersFlow: StateFlow<List<MyPokemon>>

    val pokemonTypeGroupsFlow: StateFlow<List<PokemonTypeGroup>>

    suspend fun fetchMyPocketMonsters(): List<MyPokemon>

    suspend fun fetchPokemonTypeGroups(): List<PokemonTypeGroup>

    suspend fun saveMyPocketMonster(name: String)

    suspend fun removeMyPocketMonster(catchId: Long)
}
