package me.kolotilov.lets_a_go.network

import android.content.Context
import androidx.annotation.StringRes

interface ResourceProvider {

    fun getString(@StringRes stringRes: Int): String
}

class ResourceProviderImpl(
    private val context: Context
) : ResourceProvider {

    override fun getString(stringRes: Int): String {
        return context.getString(stringRes)
    }
}