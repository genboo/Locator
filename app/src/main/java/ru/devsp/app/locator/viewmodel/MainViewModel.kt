package ru.devsp.app.locator.viewmodel

import android.arch.lifecycle.*
import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLng
import ru.devsp.app.locator.di.modules.Prefs
import ru.devsp.app.locator.model.api.LocatorApi
import ru.devsp.app.locator.model.api.Result
import ru.devsp.app.locator.tools.AppExecutors


import javax.inject.Inject


/**
 * Created by gen on 21.05.2018.
 */

class MainViewModel @Inject
internal constructor(private val appExecutor: AppExecutors,
                     private val locatorApi: LocatorApi,
                     private val prefs: SharedPreferences) : ViewModel() {

    private val sendLocationResult: MutableLiveData<Result> = MutableLiveData()

    fun sendLocation(owner: LifecycleOwner, user: String, sendTo: String, location: LatLng): LiveData<Result> {
        appExecutor.networkIO().execute {
            val request = locatorApi.setLocation(user, sendTo, location.latitude.toString(), location.longitude.toString())
            request.observe(owner, Observer {
                request.removeObservers(owner)
                sendLocationResult.postValue(it?.body)
            })
        }

        return sendLocationResult
    }

    fun saveLastLocation(lat: Double, lon: Double) {
        val editor = prefs.edit()
        editor.putFloat(Prefs.LAST_LOCATION_LAT, lat.toFloat())
        editor.putFloat(Prefs.LAST_LOCATION_LON, lon.toFloat())
        editor.apply()
    }

    fun getSavedLocation(): LatLng? {
        val lat = prefs.getFloat(Prefs.LAST_LOCATION_LAT, 0f)
        val lon = prefs.getFloat(Prefs.LAST_LOCATION_LON, 0f)
        if (lat != 0f && lon != 0f) {
            return LatLng(lat.toDouble(), lon.toDouble())
        }
        return null
    }

    fun saveParam(key: String, value: String) {
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getParam(key: String): String {
        return prefs.getString(key, "")
    }

}