package me.showang.mypokemon.fundations

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.showang.transtate.TranstateViewModel
import me.showang.transtate.core.Transform
import me.showang.transtate.core.ViewEvent
import me.showang.transtate.core.ViewState

open abstract class BaseViewModelTest<STATE : ViewState<STATE>, VM : TranstateViewModel<STATE>> {

    protected lateinit var viewModel: VM
    protected val testDispatcher = IO
    lateinit var transformQueue: MutableList<Transform<STATE>>
    lateinit var mockUpdateViewDelegate: Function1<Transform<STATE>, Unit>

    fun initTranstateObserve(viewModel: TranstateViewModel<STATE>): Job {
        transformQueue = mutableListOf()
        mockUpdateViewDelegate = mockk()
        every { mockUpdateViewDelegate.invoke(capture(transformQueue)) } just Runs
        return CoroutineScope(IO).launch {
            viewModel.newTransformFlow.collect {
                mockUpdateViewDelegate.invoke(it)
            }
        }
    }

    fun checkEvent(step: Int, checkDelegate: (ViewEvent) -> Unit) {
        transformQueue[step - 1].byEvent.apply(checkDelegate)
    }

    inline fun <EVENT : ViewEvent> check(step: Int, checkDelegate: EVENT.() -> Unit) {
        println("transformQueue: ${transformQueue.map { it.byEvent::class.simpleName }}")
        @Suppress("UNCHECKED_CAST")
        checkDelegate(transformQueue[step].byEvent as EVENT)
    }

    fun checkNoEvent() {
        assert(transformQueue.size == 1) // Initial state's event
    }

    protected fun testSuspendMethod(actions: VM.() -> Job) {
        runBlocking {
            actions.invoke(viewModel).join()
        }
    }
}
