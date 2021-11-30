package me.kolotilov.lets_a_go.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import me.kolotilov.lets_a_go.BuildConfig
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.ui.toPoint
import me.kolotilov.lets_a_go.ui.toSingle
import java.util.concurrent.TimeUnit

interface LocationService {

    fun startListen(callback: (Location) -> Unit)

    fun stopListen()
}

fun getLocationService(context: Context): LocationService {
    return when (BuildConfig.LOCATION_SERVICE) {
        "LOCAL"  -> DebugLocationServiceImpl(context)
        "DEVICE" -> ReleaseLocationServiceImpl(context)
        else     -> ReleaseLocationServiceImpl(context)
    }.apply { Log.e("BRUH", toString()) }
}

private const val INTERVAL = 2000L
private const val DISTANCE = 3f

private class DebugLocationServiceImpl(
    context: Context
) : LocationService {

    private class Listener(
        private var callback: (Location) -> Unit
    ) : LocationListener {

        fun setCallback(callback: (Location) -> Unit) {
            this.callback = callback
        }

        override fun onLocationChanged(location: Location) {
            callback(location)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
        override fun onProviderDisabled(provider: String) = Unit
        override fun onProviderEnabled(provider: String) = Unit
    }

    private val client = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var listener: Listener = Listener {}

    @SuppressLint("MissingPermission")
    override fun startListen(callback: (Location) -> Unit) {
        this.listener.setCallback(callback)
        client.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL, DISTANCE, this.listener)
    }

    override fun stopListen() {
        client.removeUpdates(listener)
        listener.setCallback {  }
    }
}

private class ReleaseLocationServiceImpl(
    context: Context
) : LocationService {

    private val client = LocationServices.getFusedLocationProviderClient(context)
    private var callback: (Location) -> Unit = {}
    private val listener = object : LocationCallback() {

        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            callback(location)
        }
    }

    @SuppressLint("MissingPermission")
    override fun startListen(callback: (Location) -> Unit) {
        this.callback = callback
        val request = LocationRequest.create()
            .setInterval(INTERVAL)
            .setFastestInterval(1000)
            .setSmallestDisplacement(DISTANCE)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        client.requestLocationUpdates(request, listener, Looper.getMainLooper())
    }

    override fun stopListen() {
        client.removeLocationUpdates(listener)
        callback = {}
    }
}

private class RxLocationServiceImpl(
    context: Context
) : LocationService {

    private val client = LocationServices.getFusedLocationProviderClient(context)
    private var callback: (Location) -> Unit = {}
    private var locationDisposable: Disposable? = null
    private val listener = object : LocationCallback() {

        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            callback(location)
        }
    }

    private var previousLocation: Point? = null

    @SuppressLint("MissingPermission")
    override fun startListen(callback: (Location) -> Unit) {
        this.callback = callback
        locationDisposable = Observable.interval(0, INTERVAL, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                val token = object : CancellationToken() {

                    override fun isCancellationRequested(): Boolean {
                        return false
                    }

                    override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                        return this
                    }
                }
                client.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, token).toSingle()
                    .toObservable()
            }
            .filter {
                val previousLocation = previousLocation
                if (previousLocation == null)
                    true
                else
                    previousLocation.distance(it.toPoint()) > DISTANCE
            }
            .retry()
            .doOnNext {
                previousLocation = it.toPoint()
                callback(it)
            }
            .subscribe({}, {})
    }

    override fun stopListen() {
        locationDisposable?.dispose()
        callback = {}
    }
}