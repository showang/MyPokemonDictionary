package me.showang.mypokemon.details.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.showang.mypokemon.details.PokemonDetailUiState
import me.showang.mypokemon.model.PokemonDetails
import me.showang.mypokemon.model.PokemonInfo

@Composable
fun DataLoadedScreen(
    modifier: Modifier = Modifier,
    state: PokemonDetailUiState.DataLoadedState,
    evolvesClickDelegate: (PokemonInfo) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PokemonDetailHeader(pokemonInfo = state.uiModel.details.info)
        PokemonTypeRow(types = state.uiModel.details.types)
        EvolvesLayout(
            modifier = Modifier,
            evolvesFromInfo = state.uiModel.evolutionInfo,
            clickDelegate = evolvesClickDelegate
        )
        Text(
            modifier = Modifier.padding(horizontal = 21.dp),
            text = state.uiModel.details.description,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DataLoadedScreenPreview() {
    DataLoadedScreen(
        state = PokemonDetailUiState.DataLoadedState(
            uiModel = PokemonDetailUiModel(
                details = PokemonDetails(
                    info = PokemonInfo(
                        monsterId = 4,
                        name = "Bulbasaur",
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png"
                    ),
                    types = listOf("Grass", "Poison"),
                    description = "A strange seed was planted on its back at birth. The plant sprouts and grows with this POKéMON.",
                    evolutionFrom = "Bulbasaur",
                ),
                evolutionInfo = PokemonInfo(
                    monsterId = 2,
                    name = "Ivysaur",
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/2.png"
                )
            ),
        ),
        evolvesClickDelegate = {}
    )
}
