package me.showang.mypokemon.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.showang.mypokemon.home.HomeUiState

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    state: HomeUiState.ErrorState
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = state.message
        )
    }
}
