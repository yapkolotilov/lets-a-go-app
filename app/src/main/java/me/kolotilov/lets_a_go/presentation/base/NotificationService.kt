package me.kolotilov.lets_a_go.presentation.base

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.ui.map.Recording
import java.util.concurrent.TimeUnit

interface NotificationService {

    fun showRecordingNotification(type: RecordingType)

    fun hideRecordingNotification()

    fun showStickToRouteNotification()

    fun hideStickToRouteNotification()
}

fun getNotificationService(context: Context, repository: Repository): NotificationService {
    return NotificationServiceImpl(context, repository)
}

private class NotificationServiceImpl(
    private val context: Context,
    private val repository: Repository
) : NotificationService {

    private companion object {

        const val RECORDING_ID = 1337
        const val STICK_TO_ROUTE_ID = 1338
        private const val CHANNEL_ID = "LETS_A_GO"
    }

    private val notificationManager = NotificationManagerCompat.from(context)
    private var stickToRouteShown: Boolean = false

    override fun showRecordingNotification(type: RecordingType) {
        createNotificationChannel()
        notificationManager.notify(RECORDING_ID, getRecodingNotification(type))
    }

    override fun hideRecordingNotification() {
        notificationManager.cancel(RECORDING_ID)
    }

    override fun showStickToRouteNotification() {
        createNotificationChannel()

        if (repository.showStickToRoute && !stickToRouteShown) {
            Completable.complete()
                .delay(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    notificationManager.notify(STICK_TO_ROUTE_ID, getStickToRouteNotification())
                    stickToRouteShown = true
                }
                .subscribe({}, {}).let { }
        }
    }

    override fun hideStickToRouteNotification() {
        notificationManager.cancel(STICK_TO_ROUTE_ID)
        stickToRouteShown = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.strict_to_route),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    @Suppress("DEPRECATION")
    private fun getStickToRouteNotification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, CHANNEL_ID)
        } else {
            NotificationCompat.Builder(context)
        }
        return builder
            .setSmallIcon(R.drawable.ic_gps_marker)
            .setContentTitle(context.getString(R.string.strict_to_route_title))
            .setContentText(context.getString(R.string.strict_to_route_text))
            .setDeleteIntent(
                PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(Recording.Action.DISMISS_STRICT_TO_ROUTE),
                    0
                )
            )
            .setOngoing(true)
            .build()
    }

    private fun getRecodingNotification(type: RecordingType): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, CHANNEL_ID)
        } else {
            NotificationCompat.Builder(context)
        }
        val title = when(type) {
            RecordingType.ROUTING -> context.getString(R.string.routing_title)
            RecordingType.ENTRYING -> context.getString(R.string.entrying_title)
        }

        return builder
            .setSmallIcon(R.drawable.ic_gps_marker)
            .setContentTitle(title)
            .setContentText(context.getString(R.string.notification_text))
            .setDeleteIntent(
                PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(Recording.Action.DISMISS_STRICT_TO_ROUTE),
                    0
                )
            )
            .setOngoing(true)
            .setNotificationSilent()
            .build()
    }

}

enum class RecordingType {

    ROUTING,
    ENTRYING
}