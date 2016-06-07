package com.github.nitrico.fluk

interface FlukView {

    fun register() = Dispatcher.register(this)
    fun unregister() = Dispatcher.unregister(this)

    fun onChange(change: Change)
    fun onError(error: Error) { }

    fun onViewRegistered() { }
    fun onViewUnregistered() { }

}
