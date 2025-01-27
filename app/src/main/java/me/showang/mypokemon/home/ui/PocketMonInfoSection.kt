package me.showang.mypokemon.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.showang.mypokemon.model.PokemonInfo

@Composable
fun PokemonInfoSection(
    modifier: Modifier = Modifier,
    titleText: String,
    sizeText: String,
    itemBuilder: LazyListScope.() -> Unit,
    lazyRowTestTag: String? = null
) {
    Column(modifier = modifier.defaultMinSize(minHeight = 196.dp)) {
        SectionTitle(titleText = titleText, itemCountText = sizeText)
        LazyRow(
            modifier = Modifier.testTag(lazyRowTestTag ?: ""),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = itemBuilder
        )
    }
}

private const val PREVIEW_ITEM_COUNT = 10

@Preview(showBackground = true)
@Composable
fun PokemonInfoSectionPreview() {
    PokemonInfoSection(
        titleText = "Pokemon",
        sizeText = "10",
        itemBuilder = {
            items(PREVIEW_ITEM_COUNT) {
                PokemonInfoItem(
                    pokemonInfo = PokemonInfo(
                        monsterId = it.toLong(),
                        name = "Pokemon $it",
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$it.png"
                    )
                )
            }
        }
    )
}
