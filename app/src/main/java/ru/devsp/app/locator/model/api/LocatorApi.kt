package ru.devsp.app.locator.model.api

import android.arch.lifecycle.LiveData
import retrofit2.http.*

import ru.devsp.app.locator.model.tools.ApiResponse

/**
 *
 * Created by gen on 02.10.2017.
 */

interface LocatorApi {

    @FormUrlEncoded
    @POST("/locator/settoken/{user}")
    fun setToken(@Path("user") user: String,  @Field("token") token: String): LiveData<ApiResponse<Result>>

}