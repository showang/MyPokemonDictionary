package me.showang.mypokemon.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.showang.mypokemon.model.MyPocketMonster
import me.showang.mypokemon.model.PocketMonInfo

@Composable
fun MyPocketHeader(
    modifier: Modifier = Modifier,
    myPocketMonsters: List<MyPocketMonster>,
    pocketMonClickDelegate: (PocketMonInfo) -> Unit,
    removeMyMonsterDelegate: (MyPocketMonster) -> Unit,
) {
    Column(modifier = modifier) {
        PocketMonInfoSection(
            titleText = "My Pocket",
            sizeText = myPocketMonsters.size.toString(),
            itemBuilder = {
                items(myPocketMonsters) { myPocketMonster ->
                    PokemonInfoItem(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true),
                            onClick = { pocketMonClickDelegate(myPocketMonster.pocketMonInfo) }
                        ),
                        pocketMonInfo = myPocketMonster.pocketMonInfo,
                        pocketBallClickDelegate = { removeMyMonsterDelegate(myPocketMonster) }
                    )
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyPocketHeaderPreview() {
    MyPocketHeader(
        myPocketMonsters = (1..10).map {
            MyPocketMonster(
                myMonsterId = it.toString(),
                pocketMonInfo = PocketMonInfo(
                    monsterId = it.toString(),
                    name = "PocketMon#$it",
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$it.png"
                )
            )
        },
        pocketMonClickDelegate = {},
        removeMyMonsterDelegate = {}
    )
}
