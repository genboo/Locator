package ru.devsp.app.locator.model.api

import android.arch.lifecycle.LiveData

import retrofit2.http.GET
import ru.devsp.app.locator.model.tools.ApiResponse

/**
 *
 * Created by gen on 02.10.2017.
 */

interface LocatorApi {

    @get:GET("/v1/sets")
    val sets: LiveData<ApiResponse<List<String>>>

}