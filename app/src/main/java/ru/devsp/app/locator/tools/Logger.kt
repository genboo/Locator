package ru.devsp.app.locator.tools

import android.util.Log
import ru.devsp.app.locator.BuildConfig

object Logger {

    private const val DEFAULT_LOGGER = BuildConfig.APPLICATION_ID

    fun e(tag: String, message: String?) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message)
        }
    }

    fun e(tag: String, message: Exception) {
        Logger.e(tag, message.message)
    }

    fun e(message: Exception) {
        Logger.e(DEFAULT_LOGGER, message.message)
    }

    fun e(message: String?) {
        Logger.e(DEFAULT_LOGGER, message)
    }

    fun d(tag: String, message: String?) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    fun d(tag: String, message: Exception) {
        Logger.d(tag, message.message)
    }

    fun d(message: Exception) {
        Logger.d(DEFAULT_LOGGER, message.message)
    }

    fun d(message: String) {
        Logger.d(DEFAULT_LOGGER, message)
    }
}
