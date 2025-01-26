package me.showang.mypokemon.home.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import me.showang.mypokemon.home.HomeUiState
import me.showang.mypokemon.model.MyPokemon
import me.showang.mypokemon.model.PokemonInfo

const val TEST_TAG_LAUNCHED_SCREEN_LAZY_COLUMN = "launched_screen_lazy_column"

@Composable
fun LaunchedScreen(
    modifier: Modifier = Modifier,
    state: HomeUiState.LaunchedState,
    onPokemonClickDelegate: (PokemonInfo) -> Unit,
    onSaveMyMonsterClickDelegate: (PokemonInfo) -> Unit,
    onRemoveMyMonsterClickDelegate: (MyPokemon) -> Unit,
) {
    LazyColumn(modifier = modifier.testTag(TEST_TAG_LAUNCHED_SCREEN_LAZY_COLUMN)) {
        item("MyPocketHeader") {
            MyPocketHeader(
                myPokemons = state.myPokemons,
                pocketMonClickDelegate = onPokemonClickDelegate,
                removeMyMonsterDelegate = onRemoveMyMonsterClickDelegate
            )
            HorizontalDivider()
        }
        items(
            items = state.typeCategories,
            key = { typeGroup -> typeGroup.pocketMonType.id }
        ) { typeGroup ->
            TypeCategorySection(
                type = typeGroup.pocketMonType,
                pocketMons = typeGroup.pocketMonsters,
                pocketMonClickDelegate = onPokemonClickDelegate,
                saveMyMonsterDelegate = onSaveMyMonsterClickDelegate,
            )
        }
    }
}
