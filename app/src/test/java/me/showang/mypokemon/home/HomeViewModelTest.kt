package me.showang.mypokemon.home

import io.mockk.Awaits
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import me.showang.mypokemon.fundations.BaseViewModelTest
import me.showang.mypokemon.model.MyPokemon
import me.showang.mypokemon.model.PokemonTypeGroup
import me.showang.mypokemon.repository.PokemonRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HomeViewModelTest : BaseViewModelTest<HomeUiState, HomeViewModel>() {

    private lateinit var mockRepository: PokemonRepository
    private var observerJob: Job? = null

    private lateinit var mockMyPocketsFlow: StateFlow<List<MyPokemon>>
    private lateinit var mockTypeGroupsFlow: StateFlow<List<PokemonTypeGroup>>
    private lateinit var myPocketCollectorSlot: CapturingSlot<FlowCollector<List<MyPokemon>>>
    private lateinit var typeGroupCollectorSlot: CapturingSlot<FlowCollector<List<PokemonTypeGroup>>>

    @BeforeTest
    fun setup() {
        myPocketCollectorSlot = CapturingSlot()
        typeGroupCollectorSlot = CapturingSlot()
        mockMyPocketsFlow = mockk {
            coEvery { collect(capture(myPocketCollectorSlot)) } just Awaits
        }
        mockTypeGroupsFlow = mockk {
            coEvery { collect(capture(typeGroupCollectorSlot)) } just Awaits
        }
        mockRepository = mockk {
            every { myPocketMonstersFlow } returns mockMyPocketsFlow
            every { pokemonTypeGroupsFlow } returns mockTypeGroupsFlow
        }
    }

    @AfterTest
    fun teardown() {
        observerJob?.cancel()
    }

    private fun initViewModel(initState: HomeUiState = HomeUiState.InitState()): HomeViewModel {
        return HomeViewModel(mockRepository, testDispatcher, initState).also {
            observerJob = initTranstateObserve(it)
        }
    }

    @Test
    fun testInitData_success() {
        val mockMyPockets: List<MyPokemon> = mockk()
        val mockTypeGroups: List<PokemonTypeGroup> = mockk()
        coEvery { mockRepository.fetchMyPocketMonsters() } returns mockMyPockets
        coEvery { mockRepository.fetchPokemonTypeGroups() } returns mockTypeGroups
        viewModel = initViewModel()
        testSuspendMethod {
            initData()
        }
        check<HomeUiEvent.InitData>(1) {
            assertEquals(mockMyPockets, myPokemons)
            assertEquals(mockTypeGroups, typeCategories)
        }
        coVerify(exactly = 1) { mockRepository.fetchMyPocketMonsters() }
        coVerify(exactly = 1) { mockRepository.fetchPokemonTypeGroups() }
    }

    @Test
    fun testInitData_failure() {
        val mockError = Exception("Mock Error")
        coEvery { mockRepository.fetchMyPocketMonsters() } throws mockError
        coEvery { mockRepository.fetchPokemonTypeGroups() } returns emptyList()

        viewModel = initViewModel()
        testSuspendMethod {
            initData()
        }
        check<HomeUiEvent.Error>(1) {
            assertEquals(mockError.message, error.message)
        }
        coVerify(exactly = 1) { mockRepository.fetchMyPocketMonsters() }
        coVerify(exactly = 1) { mockRepository.fetchPokemonTypeGroups() }
    }

    @Test
    fun testUpdateTypeGroups() {
        val mockMyPockets: List<MyPokemon> = mockk()
        val mockTypeGroups: List<PokemonTypeGroup> = mockk()
        val testTypeGroupOne: List<PokemonTypeGroup> = listOf(mockk())
        val testTypeGroupTwo: List<PokemonTypeGroup> = listOf(mockk(), mockk())
        viewModel = initViewModel(HomeUiState.LaunchedState(mockMyPockets, mockTypeGroups))

        coVerify(exactly = 1, timeout = 1000) { mockTypeGroupsFlow.collect(any()) }
        coVerify(exactly = 1, timeout = 1000) { mockMyPocketsFlow.collect(any()) }
        runBlocking {
            typeGroupCollectorSlot.captured.emit(testTypeGroupOne)
            delay(200)
            typeGroupCollectorSlot.captured.emit(testTypeGroupTwo)
            delay(200)
        }

        check<HomeUiEvent.UpdateTypeGroups>(1) {
            assertEquals(testTypeGroupOne, typeGroups)
        }
        check<HomeUiEvent.UpdateTypeGroups>(2) {
            assertEquals(testTypeGroupTwo, typeGroups)
        }
    }

    @Test
    fun testUpdateMyPocket() {
        val mockMyPockets: List<MyPokemon> = mockk()
        val mockTypeGroups: List<PokemonTypeGroup> = mockk()
        val testMyPocketOne: List<MyPokemon> = listOf(mockk())
        val testMyPocketTwo: List<MyPokemon> = listOf(mockk(), mockk())
        viewModel = initViewModel(HomeUiState.LaunchedState(mockMyPockets, mockTypeGroups))

        coVerify(exactly = 1, timeout = 1000) { mockMyPocketsFlow.collect(any()) }
        coVerify(exactly = 1, timeout = 1000) { mockTypeGroupsFlow.collect(any()) }
        runBlocking {
            myPocketCollectorSlot.captured.emit(testMyPocketOne)
            delay(200)
            myPocketCollectorSlot.captured.emit(testMyPocketTwo)
            delay(200)
        }

        check<HomeUiEvent.UpdateMyPocket>(1) {
            assertEquals(testMyPocketOne, myPokemons)
        }
        check<HomeUiEvent.UpdateMyPocket>(2) {
            assertEquals(testMyPocketTwo, myPokemons)
        }
    }

    @Test
    fun testSaveMyPokemon() {
        val testId = 123456L
        val idSlot = CapturingSlot<Long>()
        coEvery { mockRepository.saveMyPocketMonster(capture(idSlot)) }
        viewModel = initViewModel()
        testSuspendMethod {
            saveToMyPokemon(testId)
        }
        coVerify(exactly = 1) { mockRepository.saveMyPocketMonster(testId) }
    }

    @Test
    fun testRemoveMyPokemon() {
        val testId = 123456L
        val idSlot = CapturingSlot<Long>()
        coEvery { mockRepository.removeMyPocketMonster(capture(idSlot)) }
        viewModel = initViewModel()
        testSuspendMethod {
            removeFromMyPokemon(testId)
        }
        coVerify(exactly = 1) { mockRepository.removeMyPocketMonster(testId) }
    }
}
