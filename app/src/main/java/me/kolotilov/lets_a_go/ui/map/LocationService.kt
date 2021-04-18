package me.kolotilov.lets_a_go.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager

interface LocationService {

    fun startListen(callback: (Location) -> Unit)

    fun stopListen()
}

class DebugLocationServiceImpl(
    context: Context
) : LocationService {

    private val client = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var callback: (Location) -> Unit = {}

    @SuppressLint("MissingPermission")
    override fun startListen(callback: (Location) -> Unit) {
        this.callback = callback
        client.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.1f, this.callback)
    }

    override fun stopListen() {
        client.removeUpdates(callback)
    }
}