package ru.devsp.app.locator

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import io.fabric.sdk.android.Fabric
import ru.devsp.app.locator.di.components.AppComponent
import ru.devsp.app.locator.di.components.DaggerAppComponent

class App : Application() {

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
    }

}