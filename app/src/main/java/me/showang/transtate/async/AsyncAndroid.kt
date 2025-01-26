package me.showang.transtate.async

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AsyncAndroid : AsyncDelegate {
    override fun background(func: suspend () -> Unit) {
        CoroutineScope(IO).launch {
            func()
        }
    }

    override suspend fun updateUi(func: () -> Unit) = withContext(Main) { func() }

    override fun viewModelScope(viewModel: ViewModel) = viewModel.viewModelScope

    override suspend fun <Type> updateLiveDataValue(
        liveData: MutableLiveData<Type>,
        input: Type,
        post: () -> Unit
    ) {
        withContext(Main) {
            liveData.value = input
            post()
        }
    }
}
