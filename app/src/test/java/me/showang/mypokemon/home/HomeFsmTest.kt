package me.showang.mypokemon.home

import io.mockk.mockk
import me.showang.mypokemon.model.MyPocketMonster
import me.showang.mypokemon.model.PokemonTypeGroup
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    HomeFsmTest.InitStateTest::class,
    HomeFsmTest.LaunchedStateTest::class,
    HomeFsmTest.ErrorStateTest::class,
)
class HomeFsmTest {

    class InitStateTest {

        @Test
        fun `(InitState) test InitDataEvent`() {
            val myPocketList: List<MyPocketMonster> = mockk()
            val typeGroups: List<PokemonTypeGroup> = mockk()
            val target = HomeUiState.InitState()
            val testEvent = HomeUiEvent.InitData(myPocketList, typeGroups)
            target.startTransition(testEvent)?.newState.run {
                assert(this is HomeUiState.LaunchedState)
                assert((this as HomeUiState.LaunchedState).myPocketMonsters == myPocketList)
                assert(typeCategories == typeGroups)
            }
        }
    }

    class LaunchedStateTest {

        @Test
        fun `(LaunchedState) test UpdateTypeGroupsEvent`() {
            val myPocketList: List<MyPocketMonster> = mockk()
            val typeGroups: List<PokemonTypeGroup> = mockk()
            val target = HomeUiState.LaunchedState(myPocketList, typeGroups)
            val newTypeGroups: List<PokemonTypeGroup> = listOf()
            val testEvent = HomeUiEvent.UpdateTypeGroups(newTypeGroups)
            target.startTransition(testEvent)?.newState.run {
                assert(this is HomeUiState.LaunchedState)
                assert((this as HomeUiState.LaunchedState).myPocketMonsters == myPocketList)
                assert(typeCategories == newTypeGroups)
            }
        }

        @Test
        fun `(LaunchedState) test UpdateMyPocketEvent`() {
            val myPocketList: List<MyPocketMonster> = mockk()
            val typeGroups: List<PokemonTypeGroup> = mockk()
            val target = HomeUiState.LaunchedState(myPocketList, typeGroups)
            val newPocketList: List<MyPocketMonster> = listOf()
            val testEvent = HomeUiEvent.UpdateMyPocket(newPocketList)
            target.startTransition(testEvent)?.newState.run {
                assert(this is HomeUiState.LaunchedState)
                assert((this as HomeUiState.LaunchedState).typeCategories == typeGroups)
                assert(myPocketMonsters == newPocketList)
            }
        }
    }

    class ErrorStateTest {

        @Test
        fun `(ErrorState) test InitDataEvent`() {
            val myPocketList: List<MyPocketMonster> = mockk()
            val typeGroups: List<PokemonTypeGroup> = listOf()
            val target = HomeUiState.ErrorState("error")
            val testEvent = HomeUiEvent.InitData(myPocketList, typeGroups)
            target.startTransition(testEvent)?.newState.run {
                assert(this is HomeUiState.LaunchedState)
                assert((this as HomeUiState.LaunchedState).myPocketMonsters == myPocketList)
                assert(typeCategories == typeGroups)
            }
        }
    }
}
