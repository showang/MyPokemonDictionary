package me.showang.mypokemon.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter.State.Empty.painter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import me.showang.mypokemon.R
import me.showang.mypokemon.model.PokemonInfo

@Composable
fun PokemonInfoItem(
    modifier: Modifier = Modifier,
    pokemonInfo: PokemonInfo,
    pocketBallClickDelegate: (PokemonInfo) -> Unit = {},
    pocketBallTestTag: String? = null
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box {
                Image(
                    modifier = Modifier.size(100.dp),
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(pokemonInfo.imageUrl)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = null
                )
                Image(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp)
                        .align(Alignment.TopEnd)
                        .testTag(pocketBallTestTag ?: "")
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true, radius = 14.dp),
                            onClick = { pocketBallClickDelegate(pokemonInfo) }
                        ),
                    painter = painterResource(id = R.drawable.ic_pocket_ball),
                    contentDescription = "pokemon ball"
                )
            }
            Text(
                text = pokemonInfo.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
