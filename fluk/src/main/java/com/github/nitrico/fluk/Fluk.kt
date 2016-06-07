package com.github.nitrico.fluk

import android.app.Activity
import android.app.Application
import android.os.Bundle

object Fluk : Application.ActivityLifecycleCallbacks {

    private var activities = 0

    var log = false
        private set

    fun init(application: Application, showLogs: Boolean = false) {
        log = showLogs
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activities++
        if (activity is FlukView) {
            activity.register()
            activity.onViewRegistered()
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        activities--
        if (activities == 0) Dispatcher.unregisterAll()
        else if (activity is FlukView) {
            activity.unregister()
            activity.onViewUnregistered()
        }
    }

    override fun onActivityResumed(activity: Activity) { }
    override fun onActivityPaused(activity: Activity) { }
    override fun onActivityStarted(activity: Activity) { }
    override fun onActivityStopped(activity: Activity) { }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) { }

}
