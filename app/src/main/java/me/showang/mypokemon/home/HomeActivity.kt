package me.showang.mypokemon.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.showang.mypokemon.home.ui.MyPocketHeader
import me.showang.mypokemon.home.ui.TypeCategorySection
import me.showang.mypokemon.model.MyPocketMonster
import me.showang.mypokemon.model.PocketMonInfo
import me.showang.mypokemon.model.PocketMonType
import me.showang.mypokemon.model.PokemonTypeGroup
import me.showang.mypokemon.ui.theme.MyPokemonTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyPokemonTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

private const val ImageUrlTemplate = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%d.png"
private val mockMyPocketMonsters = (1..10).map {
    MyPocketMonster(
        myMonsterId = it.toString(),
        pocketMonInfo = PocketMonInfo(
            monsterId = it.toString(),
            name = "PocketMon#$it",
            imageUrl = ImageUrlTemplate.format(it)
        )
    )
}
private val mockPokemonTypeGroup = (0..14).map { index ->
    val pocketMonId = 150 - index * 10
    PokemonTypeGroup(
        pocketMonType = PocketMonType(
            id = pocketMonId.toLong(),
            name = "Type#$pocketMonId"
        ),
        pocketMonsters = (0..9).map {
            val pId = pocketMonId - it
            PocketMonInfo(
                monsterId = pId.toString(),
                name = "PocketMon#$pId",
                imageUrl = ImageUrlTemplate.format(pId)
            )
        }
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        item("MyPocketHeader") {
            MyPocketHeader(
                myPocketMonsters = mockMyPocketMonsters,
                pocketMonClickDelegate = {},
                removeMyMonsterDelegate = {}
            )
            HorizontalDivider()
        }
        val typeGroups = mockPokemonTypeGroup
        items(
            items = typeGroups,
            key = { typeGroup -> typeGroup.pocketMonType.id }
        ) { typeGroup ->
            TypeCategorySection(
                type = typeGroup.pocketMonType,
                pocketMons = typeGroup.pocketMonsters,
                pocketMonClickDelegate = {},
                saveMyMonsterDelegate = {},
            )
        }
    }
}
