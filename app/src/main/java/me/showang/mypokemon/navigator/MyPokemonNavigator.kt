package me.showang.mypokemon.navigator

import android.content.Context
import me.showang.mypokemon.model.PokemonInfo

interface MyPokemonNavigator {
    fun navigateToPokemonDetail(context: Context, pokemonInfo: PokemonInfo)
}
