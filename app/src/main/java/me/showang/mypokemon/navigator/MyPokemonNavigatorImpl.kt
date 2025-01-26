package me.showang.mypokemon.navigator

import android.content.Context
import me.showang.mypokemon.details.PokemonDetailActivity

class MyPokemonNavigatorImpl : MyPokemonNavigator {
    override fun navigateToPokemonDetail(context: Context, pokemonId: String) {
        PokemonDetailActivity.start(context, pokemonId)
    }
}
