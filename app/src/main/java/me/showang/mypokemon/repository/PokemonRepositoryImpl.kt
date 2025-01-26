package me.showang.mypokemon.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.showang.mypokemon.model.MyPokemon
import me.showang.mypokemon.model.PokemonTypeGroup

class PokemonRepositoryImpl : PokemonRepository {
    override val myPocketMonstersFlow: StateFlow<List<MyPokemon>>
        get() = MutableStateFlow(emptyList())
    override val pokemonTypeGroupsFlow: StateFlow<List<PokemonTypeGroup>>
        get() = MutableStateFlow(emptyList())

    override suspend fun saveMyPocketMonster(pokemonId: String) {
        // TODO("Not yet implemented")
    }

    override suspend fun removeMyPocketMonster(catchId: Long) {
        // TODO("Not yet implemented")
    }
}
