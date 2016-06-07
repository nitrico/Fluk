package com.github.nitrico.fluk

import android.util.Log
import rx.Subscription
import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject
import rx.subjects.Subject

internal object Dispatcher {

    private const val TAG = "Fluk"

    private val bus: Subject<Any, Any> = SerializedSubject(PublishSubject.create())
    private val actions = mutableMapOf<Store, Subscription>()
    private val changes = mutableMapOf<FlukView, Subscription>()
    private val errors = mutableMapOf<FlukView, Subscription>()

    internal fun dispatch(dispatchable: Dispatchable) = bus.onNext(dispatchable)

    internal fun register(store: Store) {
        // subscribe store to actions
        val actionSub = actions[store]
        if (actionSub == null || actionSub.isUnsubscribed) {
            actions.put(store, createActionSubscription(store))
            if (Fluk.log) Log.d(TAG, "${store.javaClass.simpleName} registered")
        }
    }

    internal fun unregister(store: Store) {
        // unsubscribe store to actions
        val actionSub = actions[store]
        if (actionSub != null && !actionSub.isUnsubscribed) {
            actions[store]?.unsubscribe()
            actions.remove(store)
            if (Fluk.log) Log.d(TAG, "${store.javaClass.simpleName} unregistered")
        }
    }

    internal fun register(view: FlukView) {
        // subscribe view to errors
        val errorSub = errors[view]
        if (errorSub == null || errorSub.isUnsubscribed) {
            errors.put(view, createErrorSubscription(view))
        }
        // subscribe view to changes
        val changeSub = changes[view]
        if (changeSub == null || changeSub.isUnsubscribed) {
            changes.put(view, createChangeSubscription(view))
            if (Fluk.log) Log.d(TAG, "${view.javaClass.simpleName} registered")
        }
    }

    internal fun unregister(view: FlukView) {
        // unsubscribe view to errors
        val errorSub = errors[view]
        if (errorSub != null && !errorSub.isUnsubscribed) {
            errors[view]?.unsubscribe()
            errors.remove(view)
        }
        // unsubscribe view to changes
        val changesSub = changes[view]
        if (changesSub != null && !changesSub.isUnsubscribed) {
            changes[view]?.unsubscribe()
            changes.remove(view)
            if (Fluk.log) Log.d(TAG, "${view.javaClass.simpleName} unregistered")
        }
    }

    private fun createActionSubscription(store: Store) = bus
            .filter { it is Action }
            .subscribe {
                store.onAction(it as Action)
                if (Fluk.log) Log.d(TAG, "${store.javaClass.simpleName} <- ${it.toDetailedString()}")
            }

    private fun createErrorSubscription(view: FlukView) = bus
            .filter { it is Error }
            .subscribe {
                view.onError(it as Error)
                if (Fluk.log) Log.d(TAG, "${view.javaClass.simpleName} <- $it")
            }

    private fun createChangeSubscription(view: FlukView) = bus
            .filter { it is Change }
            .subscribe {
                view.onChange(it as Change)
                if (Fluk.log) Log.d(TAG, "${view.javaClass.simpleName} <- $it")
            }

    @Synchronized internal fun unregisterAll() {
        for (s in actions.values) s.unsubscribe()
        for (s in changes.values) s.unsubscribe()
        for (s in errors.values) s.unsubscribe()
        actions.clear()
        changes.clear()
        errors.clear()
    }

    private fun Action.toDetailedString(): String {
        var s = javaClass.simpleName
        val fields = javaClass.declaredFields
        if (fields.size > 0) {
            s += " { "
            fields.forEach {
                it.isAccessible = true
                s += "${it.name}='${it.get(this)}', "
            }
            s = s.substring(0, s.length-2)
            s += " } "
        }
        return s
    }

}
