package me.showang.mypokemon.home

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.showang.mypokemon.TranstateHelper
import me.showang.mypokemon.espresso.CustomActivityScenarioRule
import me.showang.mypokemon.espresso.getActivityFromTestRule
import me.showang.mypokemon.home.ui.TEST_TAG_LAUNCHED_SCREEN_LAZY_COLUMN
import me.showang.mypokemon.home.ui.TEST_TAG_LOADING_SCREEN
import me.showang.mypokemon.home.ui.TEST_TAG_MY_POCKET_ITEM_BALL_FORMAT_WITH_ID
import me.showang.mypokemon.home.ui.TEST_TAG_MY_POCKET_ITEM_FORMAT_WITH_ID
import me.showang.mypokemon.home.ui.TEST_TAG_MY_POCKET_LAZY_ROW
import me.showang.mypokemon.model.MyPokemon
import me.showang.mypokemon.model.PocketMonType
import me.showang.mypokemon.model.PokemonInfo
import me.showang.mypokemon.model.PokemonTypeGroup
import me.showang.mypokemon.navigator.MyPokemonNavigator
import me.showang.transtate.TranstateViewModel
import me.showang.transtate.core.ViewState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class HomeActivityTestCases {

    @get:Rule
    val composeRule = AndroidComposeTestRule(
        activityRule = CustomActivityScenarioRule(HomeActivity::class.java, ::setup),
        activityProvider = ::getActivityFromTestRule
    )

    private lateinit var stateHelper: TranstateHelper<HomeViewModel, HomeUiState>
    private lateinit var mockViewModel: HomeViewModel
    private lateinit var mockNavigator: MyPokemonNavigator

    private fun setup() {
        mockViewModel = mockk(relaxed = true) {
            every { initData() } answers { CoroutineScope(IO).launch { } }
            stateHelper = stateHelper(HomeUiState.InitState())
        }
        mockNavigator = mockk(relaxed = true)
        loadKoinModules(
            module {
                viewModel { mockViewModel }
                single { mockNavigator }
            }
        )
    }

    @Test
    fun testLoading() {
        stateHelper.waitForComposeAttached()
        composeRule.run {
            mainClock.autoAdvance = false
            mainClock.advanceTimeByFrame()
            onNodeWithTag(TEST_TAG_LOADING_SCREEN).assertExists()
        }
    }

    @Test
    fun testLaunched_clickMyPocketItem() {
        val clickItemIndex = 9
        val clickItemId = "10"
        val clickMyPokemonId = 10L
        stateHelper.waitForComposeAttached()
        composeRule.run {
            mainClock.autoAdvance = false
            stateHelper.trigger(HomeUiEvent.InitData(mockMyPocketMonsters, mockPokemonTypeGroup))
            mainClock.advanceTimeByFrame()
            val lazyColumn = onNodeWithTag(TEST_TAG_LAUNCHED_SCREEN_LAZY_COLUMN)
            lazyColumn.assertExists()
            onNodeWithTag(TEST_TAG_MY_POCKET_LAZY_ROW).performScrollToIndex(clickItemIndex)
            val myPocketSecondItem = onNodeWithTag(TEST_TAG_MY_POCKET_ITEM_FORMAT_WITH_ID.format(clickMyPokemonId))
            myPocketSecondItem.performClick()
            val myPocketSecondItemBall =
                onNodeWithTag(TEST_TAG_MY_POCKET_ITEM_BALL_FORMAT_WITH_ID.format(clickMyPokemonId))
            myPocketSecondItemBall.performClick()

            verify(exactly = 1) { mockNavigator.navigateToPokemonDetail(any(), clickItemId) }
            verify(exactly = 1) { mockViewModel.removeFromMyPokemon(mockMyPocketMonsters[clickItemIndex]) }
        }
    }

    private fun delay(millis: Long = 1000) = runBlocking { kotlinx.coroutines.delay(millis) }

    private val imageUrlTemplate = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%d.png"
    private val mockMyPocketMonsters = (1..10).map {
        MyPokemon(
            catchId = it.toLong(),
            pokemonInfo = PokemonInfo(
                monsterId = it.toString(),
                name = "PocketMon#$it",
                imageUrl = imageUrlTemplate.format(it)
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
                PokemonInfo(
                    monsterId = pId.toString(),
                    name = "PocketMon#$pId",
                    imageUrl = imageUrlTemplate.format(pId)
                )
            }
        )
    }
}

fun <VM : TranstateViewModel<STATE>, STATE : ViewState<STATE>> VM.stateHelper(initState: STATE) =
    TranstateHelper(this, initState).apply {
        every { this@stateHelper.currentState } answers { currentState } // for onSaveInstanceState
    }
