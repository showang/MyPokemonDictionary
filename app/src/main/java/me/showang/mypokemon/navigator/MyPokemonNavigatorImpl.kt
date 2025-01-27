package me.showang.mypokemon.navigator

import android.content.Context
import me.showang.mypokemon.details.PokemonDetailActivity
import me.showang.mypokemon.model.PokemonInfo

class MyPokemonNavigatorImpl : MyPokemonNavigator {
    override fun navigateToPokemonDetail(context: Context, pokemonInfo: PokemonInfo) {
        PokemonDetailActivity.start(context, pokemonInfo)
    }
}
