package me.showang.mypokemon.repository

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import me.showang.mypokemon.model.MyPokemon
import me.showang.mypokemon.model.PokemonInfo
import me.showang.mypokemon.model.PocketMonType
import me.showang.mypokemon.model.PokemonTypeGroup

private const val ImageUrlTemplate = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%d.png"
private val mockMyPocketMonsters = (1..10).map {
    MyPokemon(
        catchId = it.toLong(),
        pokemonInfo = PokemonInfo(
            monsterId = it.toString(),
            name = "PocketMon#$it",
            imageUrl = ImageUrlTemplate.format(it)
        )
    )
}
private val mockPokemonTypeGroup = (0..14).map { index ->
    val pocketMonId = 150 - index * 10
    PokemonTypeGroup(
        pocketMonType = PocketMonType(
            id = pocketMonId.toLong(),
            name = "Type#$pocketMonId"
        ),
        pocketMonsters = (0..9).map {
            val pId = pocketMonId - it
            PokemonInfo(
                monsterId = pId.toString(),
                name = "PocketMon#$pId",
                imageUrl = ImageUrlTemplate.format(pId)
            )
        }
    )
}

interface PokemonRepository {

    val myPocketMonstersFlow: StateFlow<List<MyPokemon>>

    val pokemonTypeGroupsFlow: StateFlow<List<PokemonTypeGroup>>

    suspend fun fetchMyPocketMonsters(): List<MyPokemon> = withContext(IO) {
        mockMyPocketMonsters
    }

    suspend fun fetchPokemonTypeGroups(): List<PokemonTypeGroup> = withContext(IO) {
        mockPokemonTypeGroup
    }

    suspend fun saveMyPocketMonster(pokemonId: String)

    suspend fun removeMyPocketMonster(catchId: Long)
}
