package me.kolotilov.lets_a_go.presentation.base

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import me.kolotilov.lets_a_go.R

interface NotificationService {

    fun showStickToRouteNotification()

    fun hideStickToRouteNotification()
}

fun getNotificationService(context: Context): NotificationService {
    return NotificationServiceImpl(context)
}

private class NotificationServiceImpl(
    private val context: Context
) : NotificationService {

    private companion object {

        const val STRICT_TO_ROUTE_ID = 1338
        const val STRICT_TO_ROUTE_CHANNEL = "LETS_A_GO_STRICT_TO_ROUTE"
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    override fun showStickToRouteNotification() {
        createNotificationChannel()
        notificationManager.notify(STRICT_TO_ROUTE_ID, getNotification())
    }

    override fun hideStickToRouteNotification() {
        notificationManager.cancel(STRICT_TO_ROUTE_ID)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(STRICT_TO_ROUTE_CHANNEL, context.getString(R.string.strict_to_route), NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @Suppress("DEPRECATION")
    private fun getNotification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, STRICT_TO_ROUTE_CHANNEL)
        } else {
            NotificationCompat.Builder(context)
        }
        return builder
            .setSmallIcon(R.drawable.ic_gps_marker)
            .setContentTitle(context.getString(R.string.strict_to_route_title))
            .setContentText(context.getString(R.string.strict_to_route_text))
//            .setNotificationSilent()
            .build()
    }
}