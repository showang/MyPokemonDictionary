package me.showang.mypokemon.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import me.showang.mypokemon.home.ui.ErrorScreen
import me.showang.mypokemon.home.ui.LaunchedScreen
import me.showang.mypokemon.home.ui.LoadingScreen
import me.showang.mypokemon.navigator.MyPokemonNavigator
import me.showang.mypokemon.ui.theme.MyPokemonTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModel()
    private val navigator: MyPokemonNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        enableEdgeToEdge()
        setContent {
            MyPokemonTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun initViewModel() {
        if (viewModel.currentState is HomeUiState.InitState) {
            viewModel.initData()
        }
    }

    @Composable
    fun HomeScreen(
        modifier: Modifier = Modifier,
        viewModel: HomeViewModel = koinViewModel()
    ) {
        // Be able to use the Flow for the implementation, but the automation test foundations are not ready yet.
        val newState by viewModel.newState.observeAsState(initial = viewModel.currentState)
        when (val state = newState) {
            is HomeUiState.InitState -> LoadingScreen(modifier)
            is HomeUiState.LaunchedState -> LaunchedScreen(
                modifier = modifier,
                state = state,
                onPokemonClickDelegate = { navigator.navigateToPokemonDetail(this, it.monsterId) },
                onSaveMyMonsterClickDelegate = viewModel::saveToMyPokemon,
                onRemoveMyMonsterClickDelegate = viewModel::removeFromMyPokemon,
            )
            is HomeUiState.ErrorState -> ErrorScreen(modifier, state)
        }
    }
}
