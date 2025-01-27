package me.showang.mypokemon.db

import androidx.room.Entity
import me.showang.mypokemon.model.MyPokemon
import me.showang.mypokemon.model.PokemonInfo

const val MY_POKEMON_TABLE_NAME = "my_pokemon"

@Entity(tableName = MY_POKEMON_TABLE_NAME, primaryKeys = ["catchId"])
data class MyPokemonEntity(
    val catchId: Long,
    val name: String,
) {
    companion object {
        fun from(myPokemon: MyPokemon): MyPokemonEntity {
            return MyPokemonEntity(
                catchId = myPokemon.catchId,
                name = myPokemon.pokemonInfo.name
            )
        }
    }
}

suspend fun MyPokemonEntity.toModel(
    infoDelegate: suspend (name: String) -> PokemonInfo?
): MyPokemon? {
    return infoDelegate(name)?.let { pokemonInfo ->
        MyPokemon(
            catchId = catchId,
            pokemonInfo = pokemonInfo
        )
    }
}
