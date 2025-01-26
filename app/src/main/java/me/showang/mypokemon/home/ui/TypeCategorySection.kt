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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.showang.mypokemon.model.PocketMonInfo
import me.showang.mypokemon.model.PocketMonType

@Composable
fun TypeCategorySection(
    modifier: Modifier = Modifier,
    type: PocketMonType,
    pocketMons: List<PocketMonInfo>,
    pocketMonClickDelegate: (PocketMonInfo) -> Unit,
    saveMyMonsterDelegate: (PocketMonInfo) -> Unit,
) {
    Column(modifier) {
        PocketMonInfoSection(
            titleText = type.name,
            sizeText = pocketMons.size.toString(),
            itemBuilder = {
                items(pocketMons) { info ->
                    PokemonInfoItem(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true),
                            onClick = { pocketMonClickDelegate(info) }
                        ),
                        pocketMonInfo = info,
                        pocketBallClickDelegate = { saveMyMonsterDelegate(info) }
                    )
                }
            },
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun TypeCategorySectionPreview() {
    TypeCategorySection(
        type = PocketMonType(
            id = 1,
            name = "Type",
        ),
        pocketMons = (1..10).map {
            PocketMonInfo(
                monsterId = it.toString(),
                name = "PocketMon#$it",
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$it.png"
            )
        },
        pocketMonClickDelegate = {},
        saveMyMonsterDelegate = {}
    )
}
