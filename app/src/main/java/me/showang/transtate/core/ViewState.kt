package me.showang.transtate.core

import java.io.Serializable

abstract class ViewState<STATE>(val timestamp: Long = System.currentTimeMillis()) : Serializable {

    @Suppress("UNCHECKED_CAST")
    fun startTransition(event: ViewEvent): Transform<STATE>? {
        val preState = this as STATE
        return transition(event)?.let { newState ->
            Transform(preState, newState, event)
        }
    }

    protected abstract fun transition(byEvent: ViewEvent): STATE?
}
