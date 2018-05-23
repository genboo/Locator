package ru.devsp.app.locator.tools

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

/**
 * Управление разрешениями приложения
 * Created by gen on 11.01.2018.
 */

object PermissionsHelper {
    val PERMISSION_REQUEST_CODE_LOCATION = 1

    fun havePermissionLocation(activity: AppCompatActivity): Boolean {
        return ContextCompat
                .checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermissions(activity: AppCompatActivity) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE_LOCATION)
    }
}