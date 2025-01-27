package me.showang.mypokemon.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import me.showang.mypokemon.model.MyPokemon
import me.showang.mypokemon.model.PokemonInfo

const val TEST_TAG_MY_POCKET_LAZY_ROW = "MyPocketLazyRow"
const val TEST_TAG_MY_POCKET_ITEM_FORMAT_WITH_ID = "MyPocketItem:%d"
const val TEST_TAG_MY_POCKET_ITEM_BALL_FORMAT_WITH_ID = "MyPocketItemBall:%d"

@Composable
fun MyPocketHeader(
    modifier: Modifier = Modifier,
    myPokemons: List<MyPokemon>,
    pocketMonClickDelegate: (PokemonInfo) -> Unit,
    removeMyMonsterDelegate: (MyPokemon) -> Unit,
) {
    Column(modifier = modifier) {
        PocketMonInfoSection(
            modifier = Modifier,
            titleText = "My Pocket",
            sizeText = myPokemons.size.toString(),
            itemBuilder = {
                items(
                    myPokemons,
                    key = { it.catchId },
                ) { myPocketMonster ->
                    PokemonInfoItem(
                        modifier = Modifier
                            .testTag(TEST_TAG_MY_POCKET_ITEM_FORMAT_WITH_ID.format(myPocketMonster.catchId))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true),
                                onClick = { pocketMonClickDelegate(myPocketMonster.pokemonInfo) }
                            ),
                        pokemonInfo = myPocketMonster.pokemonInfo,
                        pocketBallClickDelegate = { removeMyMonsterDelegate(myPocketMonster) },
                        pocketBallTestTag = TEST_TAG_MY_POCKET_ITEM_BALL_FORMAT_WITH_ID.format(myPocketMonster.catchId)
                    )
                }
            },
            lazyRowTestTag = TEST_TAG_MY_POCKET_LAZY_ROW
        )
        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
fun MyPocketHeaderPreview() {
    MyPocketHeader(
        myPokemons = (1..10).map {
            MyPokemon(
                catchId = it.toLong(),
                pokemonInfo = PokemonInfo(
                    monsterId = it.toLong(),
                    name = "PocketMon#$it",
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$it.png"
                )
            )
        },
        pocketMonClickDelegate = {},
        removeMyMonsterDelegate = {}
    )
}
