package me.showang.mypokemon.home.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.showang.mypokemon.ui.theme.MyPokemonTheme

@Composable
fun SectionTitle(
    titleText: String,
    itemCountText: String
) {
    Row(
        modifier = Modifier.padding(horizontal = 21.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = titleText.run {
                val firstChar = get(0).uppercaseChar()
                replaceRange(0, 1, firstChar.toString())
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = itemCountText,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SectionTitlePreview() {
    MyPokemonTheme {
        SectionTitle(
            titleText = "title",
            itemCountText = "10"
        )
    }
}
