package me.showang.mypokemon.repository

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import me.showang.mypokemon.model.MyPokemon
import me.showang.mypokemon.model.PokemonDetails
import me.showang.mypokemon.model.PokemonInfo
import me.showang.mypokemon.model.PokemonTypeGroup

interface PokemonRepository {

    fun initData(): Job

    val myPocketMonstersFlow: StateFlow<List<MyPokemon>>

    val pokemonTypeGroupsFlow: StateFlow<List<PokemonTypeGroup>>

    suspend fun fetchMyPocketMonsters(): List<MyPokemon>

    suspend fun fetchPokemonTypeGroups(): List<PokemonTypeGroup>

    suspend fun saveMyPocketMonster(info: PokemonInfo)

    suspend fun removeMyPocketMonster(catchId: Long)

    suspend fun fetchPokemonDetails(name: String): PokemonDetails?
}
