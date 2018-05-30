package ru.devsp.app.locator.di.modules

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class Prefs {

    @Provides
    @Singleton
    fun providePrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences("main", Context.MODE_PRIVATE)
    }

    companion object {
        const val LAST_LOCATION_LAT = "last_location_lat"
        const val LAST_LOCATION_LON = "last_location_lon"
    }

}