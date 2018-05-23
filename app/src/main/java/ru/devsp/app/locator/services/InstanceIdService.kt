package ru.devsp.app.locator.services

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.iid.FirebaseInstanceIdService
import ru.devsp.app.locator.App
import ru.devsp.app.locator.di.components.AppComponent
import ru.devsp.app.locator.model.api.LocatorApi
import ru.devsp.app.locator.tools.AppExecutors
import ru.devsp.app.locator.tools.TokenSender
import ru.devsp.app.locator.view.MainActivity
import javax.inject.Inject


class InstanceIdService : FirebaseInstanceIdService() {

    private var component: AppComponent? = null

    @Inject
    lateinit var appExecutor: AppExecutors

    @Inject
    lateinit var locatorApi: LocatorApi

    override fun onTokenRefresh() {
        val prefs = baseContext.getSharedPreferences("main", Context.MODE_PRIVATE)
        val user = prefs.getString(MainActivity.SETTING_USER, "")
        if(user != "") {
            TokenSender.sendToken(appExecutor, locatorApi, user)
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (component == null) {
            component = (application as App).appComponent
            component?.inject(this)
        }
    }
}