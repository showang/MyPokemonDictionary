package me.showang.mypokemon.navigator

import android.content.Context

interface MyPokemonNavigator {
    fun navigateToPokemonDetail(context: Context, pokemonId: String)
}
