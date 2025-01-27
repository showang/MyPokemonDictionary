package me.showang.mypokemon.repository

import com.shopback.respect.core.RequestExecutor
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.showang.mypokemon.api.ApiFactory
import me.showang.mypokemon.api.PokemonBasicInfoApi
import me.showang.mypokemon.api.PokemonSpecialApi
import me.showang.mypokemon.db.MyPokemonDao
import me.showang.mypokemon.db.MyPokemonDatabase
import me.showang.mypokemon.db.PokemonDataDao
import me.showang.mypokemon.db.PokemonDataEntity
import me.showang.mypokemon.model.PokemonInfo
import me.showang.respect.RestfulApi
import kotlin.test.BeforeTest
import kotlin.test.Test

class PokemonRepositoryTest {

    private lateinit var repository: PokemonRepository

    private lateinit var mockDatabase: MyPokemonDatabase
    private lateinit var mockRequestExecutor: RequestExecutor
    private lateinit var mockApiFactory: ApiFactory

    private lateinit var mockPokemonDataDao: PokemonDataDao
    private lateinit var mockMyPokemonDao: MyPokemonDao

    @BeforeTest
    fun setup() {
        mockPokemonDataDao = mockk(relaxed = true)
        mockMyPokemonDao = mockk(relaxed = true)
        mockDatabase = mockk(relaxed = true) {
            every { pokemonDataDao() } returns mockPokemonDataDao
            every { myPokemonDao() } returns mockMyPokemonDao
        }
        mockRequestExecutor = mockk(relaxed = true)
        mockApiFactory = mockk(relaxed = true)
        repository = PokemonRepositoryImpl(mockApiFactory, mockRequestExecutor, mockDatabase)
    }

    @Test
    fun testInitData_initFromApi() {
        coEvery { mockPokemonDataDao.getAllPokemonData() } returns emptyList()
        val mockAllNameApi: RestfulApi<List<String>> = mockk {
            coEvery { request(any()) } returns listOf("bulbasaur", "ivysaur", "venusaur")
        }
        val mockBasicInfoApi: RestfulApi<PokemonBasicInfoApi.Result> = mockk {
            coEvery { request(any()) } returns PokemonBasicInfoApi.Result(
                name = "bulbasaur",
                id = 1,
                imageUrl = "https://pokeapi.co/api/v2/pokemon/1",
                types = listOf("grass", "poison")
            )
        }
        val mockPokemonSpecialApi: RestfulApi<PokemonSpecialApi.Result> = mockk {
            coEvery { request(any()) } returns PokemonSpecialApi.Result(
                name = "bulbasaur",
                evolvesFrom = "ivysaur",
                descriptions = "A strange seed was planted on its back at birth."
            )
        }
        every { mockApiFactory.createAllPokemonNameApi() } returns mockAllNameApi
        every { mockApiFactory.createPokemonBasicInfoApi(any()) } returns mockBasicInfoApi
        every { mockApiFactory.createPokemonSpecialApi(any()) } returns mockPokemonSpecialApi

        runBlocking {
            repository.initData().join()
        }

        coVerify(exactly = 1) { mockApiFactory.createAllPokemonNameApi() }
        coVerify(exactly = 3) { mockApiFactory.createPokemonBasicInfoApi(any()) }
        coVerify(exactly = 3) { mockApiFactory.createPokemonSpecialApi(any()) }

        coVerify(exactly = 1) { mockPokemonDataDao.insertPokemonList(any()) }
    }

    @Test
    fun testInitData_initFromDb() {
        val testName = "bulbasaur"
        val mockPokemonData = listOf(
            PokemonDataEntity(
                id = 1,
                name = testName,
                imageUrl = "https://pokeapi.co/api/v2/pokemon/1",
                typesArray = "grass,poison",
                description = "A strange seed was planted on its back at birth.",
                evolutionFrom = "ivysaur"
            )
        )
        coEvery { mockPokemonDataDao.getAllPokemonData() } returns mockPokemonData

        val result = runBlocking {
            repository.initData().join()
            repository.fetchPokemonTypeGroups()
        }
        assert(result.size == 2)
        result.forEach {
            assert(it.pokemonInfos.size == 1)
            assert(it.pokemonInfos.first().name == testName)
        }

        coVerify(exactly = 0) { mockApiFactory.createAllPokemonNameApi() }
        coVerify(exactly = 0) { mockApiFactory.createPokemonBasicInfoApi(any()) }
        coVerify(exactly = 0) { mockApiFactory.createPokemonSpecialApi(any()) }

        coVerify(exactly = 0) { mockPokemonDataDao.insertPokemonList(any()) }
    }

    @Test
    fun testSaveToMyPokemon() {
        val testName = "bulbasaur"
        val mockPokemonInfo: PokemonInfo = mockk {
            every { name } returns testName
        }
        runBlocking {
            repository.saveMyPocketMonster(mockPokemonInfo)
        }
        coVerify(exactly = 1) { mockMyPokemonDao.insertMyPokemon(any()) }
    }

    @Test
    fun testRemoveFromMyPokemon() {
        val myPokemonId = 1L
        coEvery { mockMyPokemonDao.deleteMyPokemonById(myPokemonId) } just Runs
        runBlocking {
            repository.removeMyPocketMonster(myPokemonId)
        }
        coVerify(exactly = 1) { mockMyPokemonDao.deleteMyPokemonById(myPokemonId) }
    }
}
