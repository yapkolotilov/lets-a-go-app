package me.kolotilov.lets_a_go.presentation.base

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

interface PermissionService {

    fun isLocationEnabled(): Boolean
}

fun getPermissionService(context: Context): PermissionService {
    return PermissionServiceImpl(context)
}

private class PermissionServiceImpl(
    private val context: Context
) : PermissionService {

    override fun isLocationEnabled(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}