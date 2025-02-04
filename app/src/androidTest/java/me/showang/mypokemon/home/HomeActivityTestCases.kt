package me.showang.mypokemon.home

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeContentTestRule
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
import me.showang.mypokemon.home.ui.TEST_TAG_TYPE_CATEGORY_ITEM_BALL_FORMAT_WITH_ID
import me.showang.mypokemon.home.ui.TEST_TAG_TYPE_CATEGORY_ITEM_FORMAT_WITH_ID
import me.showang.mypokemon.home.ui.TEST_TAG_TYPE_CATEGORY_LAZY_ROW_FORMAT_WITH_TYPE_NAME
import me.showang.mypokemon.model.MyPokemon
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
        val item = mockMyPocketMonsters[clickItemIndex]
        val clickMyPokemonId = item.catchId
        stateHelper.waitForComposeAttached()
        composeRule.run {
            mainClock.autoAdvance = false
            stateHelper.trigger(HomeUiEvent.InitData(mockMyPocketMonsters, mockPokemonTypeGroup))
            mainClock.advanceTimeByFrame()
            screenLazyColumn().assertExists()
            myPocketLazyRow().performScrollToIndex(clickItemIndex)
            myPocketItem(clickMyPokemonId).performClick()
            myPocketItemBall(clickMyPokemonId).performClick()

            verify(exactly = 1) { mockNavigator.navigateToPokemonDetail(any(), item.pokemonInfo) }
            verify(exactly = 1) { mockViewModel.removeFromMyPokemon(mockMyPocketMonsters[clickItemIndex]) }
        }
    }

    @Test
    fun testLaunched_clickTypeGroupItem() {
        val groupIndex = 12
        val groupItem = mockPokemonTypeGroup[groupIndex]
        val itemIndex = 9
        val clickItem = groupItem.pokemonInfos[itemIndex]
        stateHelper.waitForComposeAttached()
        composeRule.run {
            mainClock.autoAdvance = false
            stateHelper.trigger(HomeUiEvent.InitData(mockMyPocketMonsters, mockPokemonTypeGroup))
            mainClock.advanceTimeByFrame()
            screenLazyColumn().run {
                assertExists()
                performScrollToIndex(groupIndex)
            }
            typeGroupLazyRow(groupItem.typeName).performScrollToIndex(itemIndex)
            typeGroupInfoItem(clickItem.monsterId).performClick()
            typeGroupInfoItemBall(clickItem.monsterId).performClick()

            verify(exactly = 1) { mockNavigator.navigateToPokemonDetail(any(), clickItem) }
            verify(exactly = 1) { mockViewModel.saveToMyPokemon(clickItem) }
        }
    }

    @Test
    fun testLaunched_updateMyPocketMonsters() {
        val targetIndex = 9
        val targetItem = mockMyPocketMonsters[targetIndex]
        stateHelper.waitForComposeAttached()
        composeRule.run {
            mainClock.autoAdvance = false
            stateHelper.trigger(HomeUiEvent.InitData(emptyList(), mockPokemonTypeGroup))
            stateHelper.trigger(HomeUiEvent.UpdateMyPocket(mockMyPocketMonsters))
            mainClock.advanceTimeByFrame()
            myPocketLazyRow().run {
                assertExists()
                performScrollToIndex(9)
            }
            myPocketItem(targetItem.catchId).performClick()
            myPocketItemBall(targetItem.catchId).performClick()
            verify(exactly = 1) { mockNavigator.navigateToPokemonDetail(any(), targetItem.pokemonInfo) }
            verify(exactly = 1) { mockViewModel.removeFromMyPokemon(targetItem) }
        }
    }

    @Test
    fun testLaunched_updateTypeGroups() {
        val groupIndex = 8
        val groupItem = mockPokemonTypeGroup[groupIndex]
        val itemIndex = 8
        val clickItem = groupItem.pokemonInfos[itemIndex]
        stateHelper.waitForComposeAttached()
        composeRule.run {
            mainClock.autoAdvance = false
            stateHelper.trigger(HomeUiEvent.InitData(mockMyPocketMonsters, emptyList()))
            stateHelper.trigger(HomeUiEvent.UpdateTypeGroups(mockPokemonTypeGroup))
            mainClock.advanceTimeByFrame()
            screenLazyColumn().run {
                assertExists()
                performScrollToIndex(groupIndex)
            }
            typeGroupLazyRow(groupItem.typeName).performScrollToIndex(itemIndex)
            typeGroupInfoItem(clickItem.monsterId).performClick()
            typeGroupInfoItemBall(clickItem.monsterId).performClick()

            verify(exactly = 1) { mockNavigator.navigateToPokemonDetail(any(), clickItem) }
            verify(exactly = 1) { mockViewModel.saveToMyPokemon(clickItem) }
        }
    }

    private fun ComposeContentTestRule.screenLazyColumn() = onNodeWithTag(TEST_TAG_LAUNCHED_SCREEN_LAZY_COLUMN)
    private fun ComposeContentTestRule.myPocketLazyRow() = onNodeWithTag(TEST_TAG_MY_POCKET_LAZY_ROW)
    private fun ComposeContentTestRule.myPocketItem(id: Long) =
        onNodeWithTag(TEST_TAG_MY_POCKET_ITEM_FORMAT_WITH_ID.format(id))
    private fun ComposeContentTestRule.myPocketItemBall(
        id: Long
    ) = onNodeWithTag(TEST_TAG_MY_POCKET_ITEM_BALL_FORMAT_WITH_ID.format(id))
    private fun ComposeContentTestRule.typeGroupLazyRow(typeName: String) =
        onNodeWithTag(TEST_TAG_TYPE_CATEGORY_LAZY_ROW_FORMAT_WITH_TYPE_NAME.format(typeName))
    private fun ComposeContentTestRule.typeGroupInfoItem(id: Long) =
        onNodeWithTag(TEST_TAG_TYPE_CATEGORY_ITEM_FORMAT_WITH_ID.format(id))
    private fun ComposeContentTestRule.typeGroupInfoItemBall(id: Long) =
        onNodeWithTag(TEST_TAG_TYPE_CATEGORY_ITEM_BALL_FORMAT_WITH_ID.format(id))

//    private fun delay(millis: Long = 1000) = runBlocking { kotlinx.coroutines.delay(millis) }

    private val imageUrlTemplate = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%d.png"
    private val mockMyPocketMonsters = (1..10).map {
        MyPokemon(
            catchId = it.toLong(),
            pokemonInfo = PokemonInfo(
                monsterId = it.toLong(),
                name = "PocketMon#$it",
                imageUrl = imageUrlTemplate.format(it)
            )
        )
    }
    private val mockPokemonTypeGroup = (0..14).map { index ->
        val pocketMonId = 150 - index * 10
        PokemonTypeGroup(
            typeName = "Type#$pocketMonId",
            pokemonInfos = (0..9).map {
                val pId = pocketMonId - it
                PokemonInfo(
                    monsterId = pId.toLong(),
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
