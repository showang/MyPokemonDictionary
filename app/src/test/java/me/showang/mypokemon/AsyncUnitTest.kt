package me.showang.mypokemon

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import me.showang.transtate.async.AsyncDelegate

class AsyncUnitTest : AsyncDelegate {
    override fun background(func: suspend () -> Unit) = runBlocking {
        func()
    }

    override suspend fun updateUi(func: () -> Unit) = func()

    override fun viewModelScope(viewModel: ViewModel): CoroutineScope = CoroutineScope(Dispatchers.Default)

    override suspend fun <Type> updateLiveDataValue(liveData: MutableLiveData<Type>, input: Type, post: () -> Unit) {
        liveData.value = input
        post()
    }
}
