package com.github.nitrico.fluk

abstract class Store {

    init {
        Dispatcher.register(this)
    }

    abstract fun onAction(action: Action)

    protected fun postChange(action: Action) = Dispatcher.dispatch(Change(this, action))

    @Synchronized protected inline fun Action.react(reaction: () -> Unit) = reactTo(this, reaction)
    @Synchronized protected inline fun reactTo(action: Action, reaction: () -> Unit) {
        reaction()
        postChange(action)
    }

}
