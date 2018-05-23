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

    @FormUrlEncoded
    @POST("/locator/setlocation/{user}/{to}")
    fun setLocation(@Path("user") user: String, @Path("to") to: String,  @Field("lat") lat: String,  @Field("lon") lon: String): LiveData<ApiResponse<Result>>

    @GET("/locator/location/{user}")
    fun location(@Path("user") user: String): LiveData<ApiResponse<ResultLocation>>


    @GET("/locator/ask/{user}/{to}")
    fun ask(@Path("user") user: String, @Path("to") to: String): LiveData<ApiResponse<Result>>

}