package me.kolotilov.lets_a_go.network

import android.content.SharedPreferences

interface LocalRepository {

    var token: String
}

class LocalRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : LocalRepository {

    private companion object {
        val TOKEN = "TOKEN"
    }

    override var token: String
        get() = sharedPreferences.getString(TOKEN, "")!!
        set(value) {
            sharedPreferences.edit()
                .putString(TOKEN, value)
                .apply()
        }
}