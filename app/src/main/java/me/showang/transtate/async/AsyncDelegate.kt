package me.showang.transtate.async

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope

interface AsyncDelegate {

    fun background(func: suspend () -> Unit)

    suspend fun updateUi(func: () -> Unit)

    fun viewModelScope(viewModel: ViewModel): CoroutineScope

    suspend fun <Type> updateLiveDataValue(
        liveData: MutableLiveData<Type>,
        input: Type,
        post: () -> Unit = {}
    )
}
