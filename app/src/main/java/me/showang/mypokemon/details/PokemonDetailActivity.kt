package me.showang.mypokemon.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PokemonDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        private const val EXTRA_POKEMON_ID = "EXTRA_POKEMON_ID"

        fun start(context: Context, pokemonId: String) {
            Intent(context, PokemonDetailActivity::class.java).apply {
                putExtra(EXTRA_POKEMON_ID, pokemonId)
            }.run(context::startActivity)
        }
    }
}
