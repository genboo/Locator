package ru.devsp.app.locator.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.widget.Button
import com.google.android.gms.location.*
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import ru.devsp.app.locator.App
import ru.devsp.app.locator.R
import ru.devsp.app.locator.di.components.AppComponent
import ru.devsp.app.locator.model.api.LocatorApi
import ru.devsp.app.locator.tools.*
import ru.devsp.app.locator.viewmodel.MainViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    @Inject
    lateinit var appExecutor: AppExecutors

    @Inject
    lateinit var locatorApi: LocatorApi

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainViewModel

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

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        Logger.e("MainActivity", "token : " + FirebaseInstanceId.getInstance().token)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    val here = LatLng(location.latitude, location.longitude)
                    addMarker(here, "Ты здесь")
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

        val selectedUser = viewModel.getParam(SETTING_USER)
        if (selectedUser == "") {
            selectUserBlock.removeAllViews()
            for (i in names.indices) {
                val button = Button(this, null, 0, R.style.Button_Rounded)
                button.text = names[i]
                button.tag = users[i]
                button.setOnClickListener({ v ->
                    viewModel.saveParam(SETTING_USER, v.tag.toString())
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

    private fun askLocationSend(user: String) {
        sendLocation.slideIn(mapBlock)
        sendLocation.setOnClickListener({
            getLocationAndSend(user)
        })
    }

    @SuppressWarnings("MissingPermission")
    private fun getLocationAndSend(user: String) {
        if (PermissionsHelper.havePermissionLocation(this)) {
            val locationRequest = LocationRequest().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            sendTo = user
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            TransitionManager.beginDelayedTransition(mapBlock, Fade())
            sendLocation.fadeOut(mapBlock)
        }
    }

    private fun sendLocation(location: LatLng, sendTo: String) {
        Logger.e(MainActivity::class.java.simpleName, "send to $sendTo")
        appExecutor.networkIO().execute {
            val request = locatorApi.setLocation(viewModel.getParam(SETTING_USER), sendTo, location.latitude.toString(), location.longitude.toString())
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
                val button = ExtendedButton(this)
                button.setText(names[i])
                button.tag = users[i]
                button.setOnClickListener({ askLocation(viewModel.getParam(SETTING_USER), button.tag.toString()) })
                button.setOnExtendedButtonClickListener(View.OnClickListener { getLocationAndSend(button.tag.toString()) })
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
                            viewModel.saveLastLocation(result.lat, result.lon)
                            addMarker(position, "Пока еще тут")
                        } else {
                            Snackbar.make(mainContent, result.error?.message
                                    ?: "Координаты не получены", Snackbar.LENGTH_LONG).show()
                        }
                    })
                }
            })
        })
    }

    fun addMarker(position: LatLng, title: String) {
        map.clear()
        map.addMarker(MarkerOptions().position(position).title(title))
        map.moveCamera(CameraUpdateFactory.newLatLng(position))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, SETTING_ZO0M))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val lastPosition = viewModel.getSavedLocation()
        if (lastPosition != null) {
            addMarker(lastPosition, "Был когда-то тут")
        }
    }

    companion object {
        const val SETTING_USER = "user_id"
        const val SETTING_ZO0M = 16f
    }
}
