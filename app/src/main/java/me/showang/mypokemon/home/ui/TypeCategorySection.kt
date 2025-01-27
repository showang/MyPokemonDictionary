package me.showang.mypokemon.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.showang.mypokemon.model.PokemonInfo

const val TEST_TAG_TYPE_CATEGORY_LAZY_ROW_FORMAT_WITH_TYPE_NAME = "TypeCategoryLazyRow:%s"
const val TEST_TAG_TYPE_CATEGORY_ITEM_FORMAT_WITH_ID = "TypeCategoryItem:%d"
const val TEST_TAG_TYPE_CATEGORY_ITEM_BALL_FORMAT_WITH_ID = "TypeCategoryItemBall:%d"

@Composable
fun TypeCategorySection(
    modifier: Modifier = Modifier,
    typeName: String,
    pocketMons: List<PokemonInfo>,
    pocketMonClickDelegate: (PokemonInfo) -> Unit,
    saveMyMonsterDelegate: (PokemonInfo) -> Unit,
) {
    Column(modifier) {
        PokemonInfoSection(
            titleText = typeName,
            sizeText = pocketMons.size.toString(),
            itemBuilder = {
                items(pocketMons) { info ->
                    PokemonInfoItem(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true),
                            onClick = { pocketMonClickDelegate(info) }
                        ).testTag(TEST_TAG_TYPE_CATEGORY_ITEM_FORMAT_WITH_ID.format(info.monsterId)),
                        pokemonInfo = info,
                        pocketBallClickDelegate = { saveMyMonsterDelegate(info) },
                        pocketBallTestTag = TEST_TAG_TYPE_CATEGORY_ITEM_BALL_FORMAT_WITH_ID.format(info.monsterId)
                    )
                }
            },
            lazyRowTestTag = TEST_TAG_TYPE_CATEGORY_LAZY_ROW_FORMAT_WITH_TYPE_NAME.format(typeName)
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun TypeCategorySectionPreview() {
    TypeCategorySection(
        typeName = "Normal",
        pocketMons = (1..10).map {
            PokemonInfo(
                monsterId = it.toLong(),
                name = "PocketMon#$it",
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$it.png"
            )
        },
        pocketMonClickDelegate = {},
        saveMyMonsterDelegate = {}
    )
}
