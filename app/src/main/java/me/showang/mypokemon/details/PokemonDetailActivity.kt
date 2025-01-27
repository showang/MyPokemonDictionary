package me.showang.mypokemon.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.showang.mypokemon.model.PokemonInfo

class PokemonDetailActivity : AppCompatActivity() {

    private val pokemonInfo: PokemonInfo by lazy {
        intent.getParcelableExtra(EXTRA_POKEMON_INFO)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        private const val EXTRA_POKEMON_INFO = "EXTRA_POKEMON_INFO"

        fun start(context: Context, pokemonInfo: PokemonInfo) {
            Intent(context, PokemonDetailActivity::class.java).apply {
                putExtra(EXTRA_POKEMON_INFO, pokemonInfo)
            }.run(context::startActivity)
        }
    }
}
