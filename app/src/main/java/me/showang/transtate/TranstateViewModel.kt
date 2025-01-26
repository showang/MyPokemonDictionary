package me.showang.transtate

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.showang.mypokemon.BuildConfig
import me.showang.transtate.async.AsyncDelegate
import me.showang.transtate.core.Transform
import me.showang.transtate.core.ViewEvent
import me.showang.transtate.core.ViewState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

abstract class TranstateViewModel<STATE : ViewState<STATE>>(
    private val initState: STATE,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel(CoroutineScope(defaultDispatcher)), KoinComponent, DebugLoggable {

    protected val asyncDelegate: AsyncDelegate by inject()
    private var lastState: STATE = initState
    private val initTransform = Transform(initState, initState, ViewEvent.empty())
    private val mTransformLiveData: MutableLiveData<Transform<STATE>> by lazy {
        MutableLiveData(initTransform)
    }

    val newState: LiveData<STATE> = mTransformLiveData.map { it.newState }
    val newTransformFlow: MutableStateFlow<Transform<STATE>> = MutableStateFlow(initTransform)
    val newStateFlow = newTransformFlow.map { it.newState }
    val newEventFlow = newTransformFlow.map { it.byEvent }

    val newEvent: LiveData<ViewEvent> by lazy { mTransformLiveData.map { it.byEvent } }
    val currentState get() = lastState
    private val transformLiveData: LiveData<Transform<STATE>> get() = mTransformLiveData
    private val mutex by lazy { Mutex() }

    protected suspend fun startTransform(event: ViewEvent) {
        mutex.withLock {
            currentState.startTransition(event)?.apply {
                lastState = newState as? STATE ?: error("Unsupported state type")
            }
        }?.also { transform ->
            if (BuildConfig.DEBUG) {
                log(
                    "[${javaClass.simpleName}] ${transform.fromState.javaClass.simpleName} " +
                        "-> (${transform.byEvent.javaClass.simpleName}) " +
                        "-> ${transform.newState.javaClass.simpleName}"
                )
            }
            newTransformFlow.value = transform
            asyncDelegate.updateLiveDataValue(mTransformLiveData, transform, event::handled)
        }
    }

    fun observeTransformation(
        lifecycleOwner: LifecycleOwner,
        initViewByState: (STATE) -> Unit,
        updateViewByTransform: (Transform<STATE>) -> Unit
    ) {
        transformLiveData.observe(lifecycleOwner, generateLivedataObserver(initViewByState, updateViewByTransform))
    }

    fun observeTransformationForever(
        initViewByState: (STATE) -> Unit,
        updateViewByTransform: (Transform<STATE>) -> Unit
    ) = generateLivedataObserver(initViewByState, updateViewByTransform).apply(transformLiveData::observeForever)

    private fun generateLivedataObserver(
        initViewByState: (STATE) -> Unit,
        updateViewByTransform: (Transform<STATE>) -> Unit
    ) = Observer<Transform<STATE>> { transform ->
        transform.run {
            if (shouldHandleEvent) {
                updateViewByTransform(this)
            } else {
                initViewByState(newState)
            }
        }
    }

    protected val viewModelScope get() = asyncDelegate.viewModelScope(this)

    fun removeObserver(observer: Observer<Transform<STATE>>) = observer.let(transformLiveData::removeObserver)

    fun removeObservers(lifecycleOwner: LifecycleOwner) {
        transformLiveData.removeObservers(lifecycleOwner)
    }
}

interface DebugLoggable {
    fun <T> T.log(text: String) {
        if (BuildConfig.DEBUG) {
            Timber.e(text)
        }
    }
}
