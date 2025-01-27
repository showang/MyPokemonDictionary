package me.showang.mypokemon.repository

import com.shopback.respect.core.RequestExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import me.showang.mypokemon.api.ApiFactory
import me.showang.mypokemon.model.MyPokemon
import me.showang.mypokemon.model.PokemonDetails
import me.showang.mypokemon.model.PokemonInfo
import me.showang.mypokemon.model.PokemonTypeGroup

class PokemonRepositoryImpl(
    private val apiFactory: ApiFactory,
    private val requestExecutor: RequestExecutor
) : PokemonRepository {

    private val myPokemonMemCache = mutableListOf<MyPokemon>()
    private val pokemonTypeGroupsMemCache = mutableListOf<PokemonTypeGroup>()

    private val pokemonDetailCacheMap: MutableMap<String, PokemonDetails> = mutableMapOf()
    private val typePokemonListCacheMap: MutableMap<String, List<PokemonInfo>> = mutableMapOf()

    private val mMyPocketMonstersFlow = MutableStateFlow(myPokemonMemCache.toList())
    private val mPokemonTypeGroupsFlow = MutableStateFlow(pokemonTypeGroupsMemCache.toList())
    override val myPocketMonstersFlow: StateFlow<List<MyPokemon>>
        get() = mMyPocketMonstersFlow
    override val pokemonTypeGroupsFlow: StateFlow<List<PokemonTypeGroup>>
        get() = mPokemonTypeGroupsFlow

    private val mutex = Mutex()
    private var apiDataJob: Job? = null

    override fun initData() {
        initDataFromApi()
    }

    private fun initDataFromApi() {
        if (apiDataJob?.isActive == true) {
            return
        }
        apiDataJob = CoroutineScope(IO).launch {
            val allPokemonName = apiFactory.createAllPokemonNameApi().request(requestExecutor)
            allPokemonName.chunked(10).map { subList ->
                subList.mapNotNull { pokemonName ->
                    runCatching {
                        val pokemonTypeDeferred = async {
                            apiFactory.createPokemonTypeApi(pokemonName)
                                .request(requestExecutor)
                        }
                        val pokemonDetailsDeferred = async {
                            apiFactory.createPokemonSpecialApi(pokemonName)
                                .request(requestExecutor)
                        }
                        val pokemonType = pokemonTypeDeferred.await()
                        val pokemonDescriptions = pokemonDetailsDeferred.await()
                        PokemonDetails(
                            info = PokemonInfo(
                                monsterId = pokemonType.id.toString(),
                                name = pokemonType.name,
                                imageUrl = pokemonType.imageUrl
                            ),
                            types = pokemonType.types,
                            descriptions = pokemonDescriptions.descriptions,
                            evolutionFrom = pokemonDescriptions.evolvesFrom
                        )
                    }.getOrNull()?.let { pokemonDetails ->
                        mutex.withLock {
                            pokemonDetailCacheMap[pokemonName] = pokemonDetails
                            pokemonDetails.types.forEach { type ->
                                typePokemonListCacheMap[type] =
                                    (typePokemonListCacheMap[type] ?: emptyList()) + pokemonDetails.info
                            }
                        }
                    }
                }
                mutex.withLock {
                    updateTypeGroups()
                }
            }
        }
    }

    private fun updateTypeGroups() {
        val typeGroups = typePokemonListCacheMap.keys.sorted().map { typeName ->
            val infos = typePokemonListCacheMap[typeName]
            PokemonTypeGroup(
                typeName = typeName,
                pokemonInfos = infos ?: emptyList()
            )
        }
        pokemonTypeGroupsMemCache.clear()
        pokemonTypeGroupsMemCache.addAll(typeGroups)
        mPokemonTypeGroupsFlow.value = typeGroups
    }

    override suspend fun saveMyPocketMonster(pokemonId: String) {
        // TODO("Not yet implemented")
    }

    override suspend fun removeMyPocketMonster(catchId: Long) {
        // TODO("Not yet implemented")
    }

    override suspend fun fetchMyPocketMonsters() = withContext(IO) {
        if (myPokemonMemCache.isEmpty()) {
            // load from db
        }
        buildList {
            addAll(myPokemonMemCache)
        }
    }

    override suspend fun fetchPokemonTypeGroups(): List<PokemonTypeGroup> = withContext(IO) {
        if (pokemonTypeGroupsMemCache.isEmpty()) {
            // load from db
        }
        buildList {
            addAll(pokemonTypeGroupsMemCache)
        }
    }

    fun MutableList<PokemonInfo>.addSorted(item: PokemonInfo) {
        val index = this.binarySearch { it.name.compareTo(item.name) }
        if (index < 0) {
            this.add(-index - 1, item)
        } else {
            this.add(index, item)
        }
    }
}
