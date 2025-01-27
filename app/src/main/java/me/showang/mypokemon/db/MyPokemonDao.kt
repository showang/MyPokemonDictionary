package me.showang.mypokemon.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MyPokemonDao {

    @Insert
    suspend fun insertMyPokemon(myPokemon: MyPokemonEntity)

    @Query("SELECT * FROM $MY_POKEMON_TABLE_NAME")
    suspend fun getAllMyPokemon(): List<MyPokemonEntity>

    @Query("DELETE FROM $MY_POKEMON_TABLE_NAME WHERE catchId = :catchId")
    suspend fun deleteMyPokemonById(catchId: Long)
}
