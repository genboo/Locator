package ru.devsp.app.locator

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.crashlytics.android.Crashlytics
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import io.fabric.sdk.android.Fabric
import ru.devsp.app.locator.di.components.AppComponent
import ru.devsp.app.locator.di.components.DaggerAppComponent

class App : Application(), Application.ActivityLifecycleCallbacks {

    private var activityVisible : Boolean = false

    val appComponent: AppComponent by lazy{
        DaggerAppComponent.builder().context(this).build()
    }

    override fun onCreate() {
        super.onCreate()
        val debug = false
        val picasso = Picasso.Builder(this)
                .downloader(OkHttp3Downloader(this, 750000000))
                .indicatorsEnabled(debug)
                .loggingEnabled(debug)
                .build()
        Picasso.setSingletonInstance(picasso)
        Fabric.with(this, Crashlytics())
        registerActivityLifecycleCallbacks(this)
    }

    fun isActivityVisible(): Boolean {
        return activityVisible
    }

    override fun onActivityPaused(activity: Activity) {
        activityVisible = false
    }

    override fun onActivityResumed(activity: Activity) {
        activityVisible = true
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
    }
}