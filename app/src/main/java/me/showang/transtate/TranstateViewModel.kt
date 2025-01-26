package me.showang.transtate

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.showang.mypokemon.BuildConfig
import me.showang.transtate.core.Transform
import me.showang.transtate.core.ViewEvent
import me.showang.transtate.core.ViewState
import timber.log.Timber

abstract class TranstateViewModel<STATE : ViewState<STATE>>(
    initState: STATE,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel(CoroutineScope(coroutineDispatcher)), DebugLoggable {

    private var lastState: STATE = initState

    private val initTransform = Transform(initState, initState, ViewEvent.empty())
    val newTransformFlow: MutableStateFlow<Transform<STATE>> = MutableStateFlow(initTransform)
    val newStateFlow = newTransformFlow.map { it.newState }
    val newEventFlow = newTransformFlow.map { it.byEvent }

    val currentState get() = lastState
    private val mutex by lazy { Mutex() }

    protected suspend fun startTransform(event: ViewEvent) {
        mutex.withLock {
            currentState.startTransition(event)?.apply {
                lastState = newState as? STATE ?: error("Unsupported state type")
            }
        }?.also { transform ->
            log(
                "[${javaClass.simpleName}] ${transform.fromState.javaClass.simpleName} " +
                    "-> (${transform.byEvent.javaClass.simpleName}) " +
                    "-> ${transform.newState.javaClass.simpleName}"
            )
            newTransformFlow.value = transform
        }
    }
}

interface DebugLoggable {
    fun <T> T.log(text: String) {
        if (BuildConfig.DEBUG) {
            Timber.e(text)
        }
    }
}
