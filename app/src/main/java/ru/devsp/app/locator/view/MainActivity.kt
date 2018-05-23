package ru.devsp.app.locator.view

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Button
import com.google.android.gms.location.*
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import ru.devsp.app.locator.App
import ru.devsp.app.locator.R
import ru.devsp.app.locator.di.components.AppComponent
import ru.devsp.app.locator.model.api.LocatorApi
import ru.devsp.app.locator.tools.AppExecutors
import ru.devsp.app.locator.tools.Logger
import ru.devsp.app.locator.tools.PermissionsHelper
import ru.devsp.app.locator.tools.TokenSender
import javax.inject.Inject

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    @Inject
    lateinit var appExecutor: AppExecutors

    @Inject
    lateinit var locatorApi: LocatorApi

    private lateinit var map: GoogleMap
    private lateinit var locationCallback: LocationCallback

    private var component: AppComponent? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var users: Array<String>
    private lateinit var names: Array<String>
    private var sendTo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (component == null) {
            component = (application as App).appComponent
            component?.inject(this)
        }

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    val here = LatLng(location.latitude, location.longitude)
                    map.addMarker(MarkerOptions().position(here).title("Ты здесь"))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(here, SETTING_ZO0M))
                    sendLocation(here, sendTo)
                    break
                }
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }

        if (!PermissionsHelper.havePermissionLocation(this)) {
            PermissionsHelper.requestLocationPermissions(this)
        }
        processIntent(intent)

        names = resources.getStringArray(R.array.names)
        users = resources.getStringArray(R.array.users)

        val selectedUser = getParam(SETTING_USER)
        if (selectedUser == "") {
            selectUserBlock.removeAllViews()
            for (i in names.indices) {
                val button = Button(this)
                button.text = names[i]
                button.tag = users[i]
                button.setOnClickListener({ v ->
                    saveParam(SETTING_USER, v.tag.toString())
                    prepareMapBlock(v.tag.toString())
                    TokenSender.sendToken(appExecutor, locatorApi, v.tag.toString())
                })
                selectUserBlock.addView(button)
            }
        } else {
            prepareMapBlock(selectedUser)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        processIntent(intent)
    }

    private fun processIntent(intent: Intent) {
        when (intent.getStringExtra("action")) {
            "sendLocation" -> askLocationSend(intent.getStringExtra("user"))
            "loadLocation" -> loadLocation(intent.getStringExtra("user"))
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun askLocationSend(user: String) {
        val snackBar = Snackbar.make(mainContent, "Отправить координаты?", Snackbar.LENGTH_LONG)
        snackBar.setAction("Да", {
            if (PermissionsHelper.havePermissionLocation(this)) {
                val locationRequest = LocationRequest().apply {
                    interval = 10000
                    fastestInterval = 5000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                sendTo = user
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            }
        }).show()
    }

    private fun sendLocation(location: LatLng, sendTo: String) {
        Logger.e(MainActivity::class.java.simpleName, "send to $sendTo")
        appExecutor.networkIO().execute {
            val request = locatorApi.setLocation(getParam(SETTING_USER), sendTo, location.latitude.toString(), location.longitude.toString())
            request.observe(this, Observer {
                request.removeObservers(this)
                Snackbar.make(mainContent, "Отправлено", Snackbar.LENGTH_LONG).show()
            })
        }
    }


    private fun prepareMapBlock(excludedUser: String) {
        selectUserBlock.visibility = View.GONE
        mapBlock.visibility = View.VISIBLE
        usersBlock.removeAllViews()
        for (i in names.indices) {
            if (users[i] != excludedUser) {
                val button = Button(this)
                button.text = names[i]
                button.tag = users[i]
                button.setOnClickListener({ askLocation(getParam(SETTING_USER), it.tag.toString()) })
                usersBlock.addView(button)
            }
        }
    }

    private fun askLocation(user: String, askTo: String) {
        appExecutor.networkIO().execute({
            locatorApi.ask(user, askTo).observe(this, Observer {
                val result = it?.body
                if (result != null) {
                    appExecutor.mainThread().execute({
                        if (result.error == null) {
                            Snackbar.make(mainContent, "Местоположение запрошено", Snackbar.LENGTH_LONG).show()
                        } else {
                            Snackbar.make(mainContent, result.error?.message
                                    ?: "Пользователь не найден", Snackbar.LENGTH_LONG).show()
                        }
                    })
                }
            })
        })
    }

    private fun loadLocation(user: String) {
        appExecutor.networkIO().execute({
            locatorApi.location(user).observe(this, Observer {
                val result = it?.body
                if (result != null) {
                    appExecutor.mainThread().execute({
                        if (result.error == null) {
                            val position = LatLng(result.lat, result.lon)
                            map.addMarker(MarkerOptions().position(position).title("Пока еще тут"))
                            map.moveCamera(CameraUpdateFactory.newLatLng(position))
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, SETTING_ZO0M))
                        } else {
                            Snackbar.make(mainContent, result.error?.message
                                    ?: "Координаты не получены", Snackbar.LENGTH_LONG).show()
                        }
                    })
                }
            })
        })
    }

    private fun saveParam(key: String, value: String) {
        val prefs = getSharedPreferences("main", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    private fun getParam(key: String): String {
        val prefs = getSharedPreferences("main", Context.MODE_PRIVATE)
        return prefs.getString(key, "")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val uiSettings = map.uiSettings
        uiSettings.isZoomControlsEnabled = true
    }

    companion object {
        const val SETTING_USER = "user_id"
        const val SETTING_ZO0M = 16f
    }
}
