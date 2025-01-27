package me.showang.mypokemon.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PokemonDataEntity::class, MyPokemonEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MyPokemonDatabase : RoomDatabase() {
    abstract fun pokemonDataDao(): PokemonDataDao
    abstract fun myPokemonDao(): MyPokemonDao

    companion object {
        const val DATABASE_NAME = "my_pokemon_database"
    }
}
