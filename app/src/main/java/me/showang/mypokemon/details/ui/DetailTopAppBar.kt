package me.showang.mypokemon.details.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter.State.Empty.painter
import me.showang.mypokemon.R

@Composable
fun DetailTopAppBar(
    modifier: Modifier = Modifier,
    pokemonId: Long,
    onBackClick: () -> Unit
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(42.dp)
                .padding(end = 16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = onBackClick
                ),
            painter = painterResource(id = R.drawable.ic_back),
            tint = Color.Black,
            contentDescription = "Back"
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            "#$pokemonId",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailTopAppBarPreview() {
    DetailTopAppBar(
        pokemonId = 1,
        onBackClick = {}
    )
}
