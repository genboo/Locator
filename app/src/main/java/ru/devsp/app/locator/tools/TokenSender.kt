package ru.devsp.app.locator.tools

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import com.google.firebase.iid.FirebaseInstanceId
import ru.devsp.app.locator.model.api.LocatorApi
import ru.devsp.app.locator.model.api.Result
import ru.devsp.app.locator.model.tools.ApiResponse

object TokenSender : Observer<ApiResponse<Result>> {

    private lateinit var request: LiveData<ApiResponse<Result>>

    fun sendToken(appExecutor: AppExecutors, locatorApi: LocatorApi, user: String) {
        appExecutor.networkIO().execute {
            val refreshedToken = FirebaseInstanceId.getInstance().token ?: ""
            Logger.e("TokenSender", "token : $refreshedToken")
            request = locatorApi.setToken(user, refreshedToken)
            request.observeForever(this)
        }
    }

    override fun onChanged(result: ApiResponse<Result>?) {
        request.removeObserver(this)
        val flag = result?.body?.result ?: false
        if (flag) {
            Logger.e("TokenSender", "token saved")
        } else {
            Logger.e("TokenSender", "token not saved")
        }
    }
}