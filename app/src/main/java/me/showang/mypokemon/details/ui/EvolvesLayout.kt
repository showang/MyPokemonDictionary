package me.showang.mypokemon.details.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import me.showang.mypokemon.model.PokemonInfo

@Composable
fun EvolvesLayout(
    modifier: Modifier = Modifier,
    evolvesFromInfo: PokemonInfo?,
    clickDelegate: (PokemonInfo) -> Unit,
) {
    Box(
        modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = true),
            onClick = {
                evolvesFromInfo?.let(clickDelegate)
            }
        )
    ) {
        Row(Modifier.padding(horizontal = 21.dp, vertical = 8.dp)) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Evolves from",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = evolvesFromInfo?.displayName ?: "No evolves",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
            }
            evolvesFromInfo?.let { info ->
                Image(
                    modifier = Modifier.size(64.dp),
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(info.imageUrl)
                            .crossfade(true)
                            .build(),
                    ),
                    contentDescription = "Evolves from image: ${info.name}"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EvolvesLayoutPreview() {
    EvolvesLayout(
        evolvesFromInfo = PokemonInfo(
            monsterId = 2,
            name = "Ivysaur",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/2.png"
        ),
        clickDelegate = {}
    )
}
