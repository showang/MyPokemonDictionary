package me.showang.mypokemon

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.showang.transtate.TranstateViewModel
import me.showang.transtate.core.Transform
import me.showang.transtate.core.ViewEvent
import me.showang.transtate.core.ViewState

class TranstateHelper<VM : TranstateViewModel<STATE>, STATE : ViewState<STATE>>(private val mockViewModel: VM, initState: STATE? = null) :
    StateBuilder<STATE> {
    private var initViewDelegateSlot = CapturingSlot<(STATE) -> Unit>()
    private val updateViewDelegateList = mutableListOf<(Transform<STATE>) -> Unit>()
    private var newStateObserverSlot = CapturingSlot<Observer<STATE>>()
    private var initTransformDelegate: () -> Unit = {}
    private var initNewStateDelegate: () -> Unit = {}
    private var mockNewStateLiveData: LiveData<STATE> = mockk<LiveData<STATE>> {
        every { observe(any(), capture(newStateObserverSlot)) } answers {
            initNewStateDelegate()
        }
        every { value } answers { lastState }
        every { isInitialized } returns true
        every { removeObserver(any()) } just Runs
    }
    private lateinit var lastState: STATE
    val currentState get() = lastState

    init {
        every {
            mockViewModel.observeTransformation(any(), capture(initViewDelegateSlot), capture(updateViewDelegateList))
        } answers { initTransformDelegate() }
        every { mockViewModel.newState } answers { mockNewStateLiveData }
        initState?.let { initTransitions(it) }
    }

    infix fun initTransitions(initState: STATE) {
        initTransitions(initState) {}
    }

    fun initTransitions(initState: STATE, delegate: StateBuilder<STATE>.() -> Unit) {
        lastState = initState
        initTransformDelegate = {
            init(lastState)
            delegate(this)
        }
        initNewStateDelegate = {
            newStateObserverSlot.captured.onChanged(initState)
        }
    }

    fun initWithoutFragment() {
        mockViewModel.observeTransformation(mockk(), {}, {})
        initTransformDelegate()
    }

    override fun init(state: STATE) {
        initViewDelegateSlot.captured(state)
    }

    override infix fun trigger(event: ViewEvent) {
        runBlocking {
            lastState.startTransition(event)!!.let { transform ->
                withContext(Main) {
                    updateViewDelegateList.forEach { it.invoke(transform) }
                    updateNewStateObserver(transform.newState)
                }
                lastState = transform.newState
            }
        }
    }

    override fun updateBy(eventToState: Pair<ViewEvent, STATE>) {
        update(eventToState.second, eventToState.first)
    }

    override fun update(to: STATE, by: ViewEvent) {
        if (!::lastState.isInitialized) error("init state must be defined before trigger")
        force(lastState, by, to)
    }

    override fun force(from: STATE, byEvent: ViewEvent, to: STATE) {
        Transform(from, to, byEvent).run {
            updateViewDelegateList.forEach { it.invoke(this) }
        }
        updateNewStateObserver(to)
        lastState = to
    }

    fun waitForAttached(): TranstateHelper<VM, STATE> {
        verify(timeout = 10000) { mockViewModel.observeTransformation(any(), any(), any()) }
        return this
    }

    fun waitForComposeAttached(): TranstateHelper<VM, STATE> {
        verify(timeout = 10000) { mockViewModel.newState }
        return this
    }

    private fun updateNewStateObserver(newState: STATE) {
        if (newStateObserverSlot.isCaptured) {
            newStateObserverSlot.captured.onChanged(newState)
        }
    }
}

interface StateBuilder<STATE : ViewState<STATE>> {
    fun init(state: STATE)
    fun trigger(event: ViewEvent)
    fun updateBy(eventToState: Pair<ViewEvent, STATE>)
    fun update(to: STATE, by: ViewEvent)
    fun force(from: STATE, byEvent: ViewEvent, to: STATE)
}
