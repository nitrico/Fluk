package com.github.nitrico.fluk

interface Dispatchable

class Error(val action: Action, val throwable: Throwable) : Dispatchable {
    override fun toString() = "${action.javaClass.simpleName} ~ ${throwable.message}"
}

class Change(val store: Store, val action: Action) : Dispatchable {
    override fun toString() = "${action.javaClass.simpleName} (${store.javaClass.simpleName})"
}

open class Action : Dispatchable {
    protected fun postAction() = Dispatcher.dispatch(this)
    protected fun postError(throwable: Throwable) = Dispatcher.dispatch(Error(this, throwable))
}

open class SimpleAction : Action() {
    init {
        postAction()
    }
}
