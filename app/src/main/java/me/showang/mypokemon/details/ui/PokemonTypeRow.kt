package me.showang.mypokemon.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PokemonTypeRow(
    modifier: Modifier = Modifier,
    types: List<String>
) {
    Row(
        modifier.padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        types.forEach {
            PokemonTypeItem(type = it)
        }
    }
}

@Composable
fun PokemonTypeItem(
    modifier: Modifier = Modifier,
    type: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
    ) {
        Text(
            modifier = Modifier
                .background(Color.DarkGray.copy(alpha = 0.2f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            text = type,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonTypeRowPreview() {
    PokemonTypeRow(types = listOf("Grass", "Poison"))
}
