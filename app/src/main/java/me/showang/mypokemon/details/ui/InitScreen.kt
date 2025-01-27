package me.showang.mypokemon.details.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.showang.mypokemon.details.PokemonDetailUiState
import me.showang.mypokemon.model.PokemonInfo

@Composable
fun InitScreen(
    modifier: Modifier = Modifier,
    state: PokemonDetailUiState.InitState,
) {
    val pokemonInfo = state.pokemonInfo
    PokemonDetailHeader(pokemonInfo = pokemonInfo)
}

@Preview(showBackground = true)
@Composable
fun InitScreenPreview() {
    InitScreen(
        state = PokemonDetailUiState.InitState(
            pokemonInfo = PokemonInfo(
                monsterId = 4,
                name = "Bulbasaur",
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png"
            )
        )
    )
}
