package me.showang.mypokemon.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import me.showang.mypokemon.details.ui.DataLoadedScreen
import me.showang.mypokemon.details.ui.DetailTopAppBar
import me.showang.mypokemon.details.ui.InitScreen
import me.showang.mypokemon.home.ui.ErrorScreen
import me.showang.mypokemon.model.PokemonInfo
import me.showang.mypokemon.navigator.MyPokemonNavigator
import me.showang.mypokemon.ui.theme.MyPokemonTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PokemonDetailActivity : AppCompatActivity() {

    private val pokemonInfo: PokemonInfo by lazy {
        intent.getParcelableExtra(EXTRA_POKEMON_INFO)!!
    }

    private val navigator: MyPokemonNavigator by inject()
    private val viewModel: PokemonDetailViewModel by viewModel {
        parametersOf(pokemonInfo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init()
        enableEdgeToEdge()
        setContent {
            MyPokemonTheme {
                Scaffold(
                    topBar = {
                        DetailTopAppBar(
                            modifier = Modifier.systemBarsPadding(),
                            pokemonId = pokemonInfo.monsterId,
                            onBackClick = ::onBackPressed
                        )
                    }
                ) { innerPadding ->
                    PokemonDetailScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @Composable
    fun PokemonDetailScreen(
        modifier: Modifier = Modifier,
        viewModel: PokemonDetailViewModel = koinViewModel()
    ) {
        // Be able to use the Flow for the implementation, but the automation test foundations are not ready yet.
        val newState by viewModel.newState.observeAsState(initial = viewModel.currentState)
        when (val state = newState) {
            is PokemonDetailUiState.InitState -> InitScreen(modifier, state = state)
            is PokemonDetailUiState.DataLoadedState -> DataLoadedScreen(
                modifier = modifier,
                state = state,
                evolvesClickDelegate = { navigator.navigateToPokemonDetail(this, it) }
            )
            is PokemonDetailUiState.ErrorState -> ErrorScreen(modifier, errorMessage = state.message)
        }
    }

    companion object {
        private const val EXTRA_POKEMON_INFO = "EXTRA_POKEMON_INFO"

        fun start(context: Context, pokemonInfo: PokemonInfo) {
            Intent(context, PokemonDetailActivity::class.java).apply {
                putExtra(EXTRA_POKEMON_INFO, pokemonInfo)
            }.run(context::startActivity)
        }
    }
}
