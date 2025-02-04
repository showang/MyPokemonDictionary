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
import me.showang.mypokemon.db.MyPokemonDatabase
import me.showang.mypokemon.db.MyPokemonEntity
import me.showang.mypokemon.db.PokemonDataEntity
import me.showang.mypokemon.db.toModel
import me.showang.mypokemon.db.toPokemonDetails
import me.showang.mypokemon.model.MyPokemon
import me.showang.mypokemon.model.PokemonDetails
import me.showang.mypokemon.model.PokemonInfo
import me.showang.mypokemon.model.PokemonTypeGroup

class PokemonRepositoryImpl(
    private val apiFactory: ApiFactory,
    private val requestExecutor: RequestExecutor,
    private val database: MyPokemonDatabase,
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

    override fun initData() = CoroutineScope(IO).launch {
        val allPokemonEntities = database.pokemonDataDao().getAllPokemonData()
        if (allPokemonEntities.isNotEmpty()) {
            // load from db
            initDataFromDb(allPokemonEntities)
        } else {
            initDataFromApi()
        }
    }

    private suspend fun initDataFromDb(entities: List<PokemonDataEntity>) = withContext(IO) {
        entities.chunked(SIZE_CHUNKED_FOR_BATCH).map { sublist ->
            async {
                sublist.forEach { entity ->
                    val pokemonDetails = entity.toPokemonDetails()
                    mutex.withLock {
                        pokemonDetailCacheMap[entity.name] = pokemonDetails
                    }
                    pokemonDetails.types.forEach { type ->
                        mutex.withLock {
                            val typePokemonList = typePokemonListCacheMap[type] ?: emptyList()
                            typePokemonListCacheMap[type] =
                                typePokemonList.toMutableList().apply {
                                    addSorted(pokemonDetails.info)
                                }
                        }
                    }
                }
            }
        }.forEach { it.await() }
        mutex.withLock {
            updateTypeGroups()
        }
    }

    private fun CoroutineScope.initDataFromApi() {
        if (apiDataJob?.isActive == true) {
            return
        }
        apiDataJob = launch {
            val allPokemonName = apiFactory.createAllPokemonNameApi().request(requestExecutor)
            allPokemonName.chunked(SIZE_CHUNKED_FOR_BATCH).map {
                async {
                    batchFetchingData(it)
                }
            }.forEach { it.await() }
            snapshotPokemonDataToDb()
        }
    }

    private suspend fun snapshotPokemonDataToDb() {
        mutex.withLock {
            database.pokemonDataDao()
                .insertPokemonList(pokemonDetailCacheMap.values.map(PokemonDataEntity::from))
        }
    }

    private suspend fun batchFetchingData(nameList: List<String>) = withContext(IO) {
        nameList.mapNotNull { pokemonName ->
            runCatching {
                val pokemonTypeDeferred = async {
                    apiFactory.createPokemonBasicInfoApi(pokemonName)
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
                        monsterId = pokemonType.id,
                        name = pokemonType.name,
                        imageUrl = pokemonType.imageUrl
                    ),
                    types = pokemonType.types,
                    description = pokemonDescriptions.descriptions,
                    evolutionFrom = pokemonDescriptions.evolvesFrom
                )
            }.getOrNull()?.let { pokemonDetails ->
                mutex.withLock {
                    pokemonDetailCacheMap[pokemonName] = pokemonDetails
                    pokemonDetails.types.forEach { type ->
                        val typePokemonList = typePokemonListCacheMap[type] ?: emptyList()
                        typePokemonListCacheMap[type] = typePokemonList.toMutableList().apply {
                            addSorted(pokemonDetails.info)
                        }
                    }
                }
            }
        }
        mutex.withLock {
            updateTypeGroups()
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

    override suspend fun saveMyPocketMonster(info: PokemonInfo) {
        if (myPokemonMemCache.size >= 300) return
        val myPokemon = MyPokemon(
            catchId = System.currentTimeMillis(),
            pokemonInfo = info
        )
        myPokemonMemCache.add(myPokemon)
        mMyPocketMonstersFlow.value = myPokemonMemCache.toList()
        database.myPokemonDao().insertMyPokemon(MyPokemonEntity.from(myPokemon))
    }

    override suspend fun removeMyPocketMonster(catchId: Long) {
        if (myPokemonMemCache.removeIf { it.catchId == catchId }) {
            mMyPocketMonstersFlow.value = myPokemonMemCache.toList()
        }
        database.myPokemonDao().deleteMyPokemonById(catchId)
    }

    override suspend fun fetchMyPocketMonsters() = withContext(IO) {
        if (myPokemonMemCache.isEmpty()) {
            val myPokemonList = database.myPokemonDao().getAllMyPokemon().mapNotNull {
                it.toModel { name ->
                    pokemonDetailCacheMap[name]?.info
                        ?: database.pokemonDataDao().getPokemonByName(name)?.toPokemonDetails()?.info
                }
            }
            myPokemonMemCache.addAll(myPokemonList)
        }
        buildList {
            addAll(myPokemonMemCache)
        }
    }

    override suspend fun fetchPokemonTypeGroups(): List<PokemonTypeGroup> = withContext(IO) {
        buildList {
            mutex.withLock {
                addAll(pokemonTypeGroupsMemCache)
            }
        }
    }

    override suspend fun fetchPokemonDetails(name: String): PokemonDetails? {
        return pokemonDetailCacheMap[name]
            ?: database.pokemonDataDao().getPokemonByName(name)?.toPokemonDetails()
    }

    private fun MutableList<PokemonInfo>.addSorted(item: PokemonInfo) {
        val index = this.binarySearch { it.monsterId.compareTo(item.monsterId) }
        if (index == 0) {
            return // already exist
        }
        if (index < 0) {
            this.add(-index - 1, item)
        } else {
            this.add(index, item)
        }
    }

    companion object {
        private const val SIZE_CHUNKED_FOR_BATCH = 10
    }
}
