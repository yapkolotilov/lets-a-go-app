package me.kolotilov.lets_a_go.network

import android.content.SharedPreferences
import androidx.core.content.edit
import me.kolotilov.lets_a_go.models.Point
import org.joda.time.DateTime

interface LocalRepository {

    var token: String

    var lastLocation: Point?
}

class LocalRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : LocalRepository {

    private companion object {

        const val TOKEN = "TOKEN"

        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"
        const val ALTITUDE = "ALTITUDE"
    }

    override var token: String
        get() = sharedPreferences.getString(TOKEN, "")!!
        set(value) {
            sharedPreferences.edit()
                .putString(TOKEN, value)
                .apply()
        }

    override var lastLocation: Point?
        get() {
            val latitude = sharedPreferences.getFloat(LATITUDE, -1f).toDouble().takeIf { it != -1.0 }
            val longitude = sharedPreferences.getFloat(LONGITUDE, -1f).toDouble().takeIf { it != -1.0 }
            val altitude = sharedPreferences.getFloat(ALTITUDE, -1f).toDouble().takeIf { it != -1.0 }
            return if (latitude != null && longitude != null && altitude != null)
                Point(latitude, longitude, altitude, DateTime.now())
            else
                null
        }
        set(value) {
            sharedPreferences.edit {
                putFloat(LATITUDE, value?.latitude?.toFloat() ?: -1f)
                putFloat(LONGITUDE, value?.longitude?.toFloat() ?: -1f)
                putFloat(ALTITUDE, value?.altitude?.toFloat() ?: -1f)
            }
        }
}