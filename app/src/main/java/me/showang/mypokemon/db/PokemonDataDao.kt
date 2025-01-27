package me.showang.mypokemon.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PokemonDataDao {

    @Insert
    suspend fun insertPokemon(pokemon: PokemonDataEntity)

    @Insert
    suspend fun insertPokemonList(pokemonList: List<PokemonDataEntity>)

    @Query("SELECT * FROM $TABLE_NAME_POKEMON_DATA")
    suspend fun getAllPokemonData(): List<PokemonDataEntity>

    @Query("SELECT * FROM $TABLE_NAME_POKEMON_DATA WHERE id = :id")
    suspend fun getPokemonById(id: Long): PokemonDataEntity?

    @Query("SELECT * FROM $TABLE_NAME_POKEMON_DATA WHERE name = :name")
    suspend fun getPokemonByName(name: String): PokemonDataEntity?

    @Update
    suspend fun updatePokemon(pokemon: PokemonDataEntity)

    @Query("DELETE FROM $TABLE_NAME_POKEMON_DATA WHERE id = :id")
    suspend fun deletePokemonById(id: Long)
}