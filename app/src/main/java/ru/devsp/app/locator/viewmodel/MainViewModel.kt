package ru.devsp.app.locator.viewmodel

import android.arch.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import ru.devsp.app.locator.model.api.LocatorApi
import ru.devsp.app.locator.model.api.Result
import ru.devsp.app.locator.tools.AppExecutors


import javax.inject.Inject


/**
 * Created by gen on 21.05.2018.
 */

class MainViewModel @Inject
internal constructor(private val appExecutor: AppExecutors,
                     private val locatorApi: LocatorApi) : ViewModel() {

    private val sendLocationResult: MutableLiveData<Result> = MutableLiveData()

    fun sendLocation(owner: LifecycleOwner, user: String, sendTo: String, location: LatLng) : LiveData<Result> {
        appExecutor.networkIO().execute {
            val request = locatorApi.setLocation(user, sendTo, location.latitude.toString(), location.longitude.toString())
            request.observe(owner, Observer {
                request.removeObservers(owner)
                sendLocationResult.postValue(it?.body)
            })
        }

        return sendLocationResult
    }

}