package me.kolotilov.lets_a_go.ui.map

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.ui.PointParam
import me.kolotilov.lets_a_go.ui.base.BaseService
import me.kolotilov.lets_a_go.ui.base.MainActivity
import me.kolotilov.lets_a_go.ui.toPoint
import me.kolotilov.lets_a_go.ui.toPointParam
import me.kolotilov.lets_a_go.utils.castTo
import org.kodein.di.instance
import java.io.Serializable


class MapService : BaseService() {

    companion object {

        private const val ID = 1337
        private const val CHANNEL_ID = "LETS_A_GO"

        fun start(context: Context, data: RecordingData) {
            val intent = Intent(context, MapService::class.java).apply {
                action = Recording.Action.START_RECORDING
                putExtra(Recording.RECORDING, data.toRecordingParam())
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else
                context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(Recording.Action.STOP_RECORDING)
            context.sendBroadcast(intent)
        }
    }

    override val viewModel: MapServiceViewModel by instance()
    private val locationService: LocationService by instance()

    private val stopReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Recording.Action.STOP_RECORDING) {
                MainActivity.start(context, viewModel.getRecordingData())
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationService.stopListen()
        unregisterReceiver(stopReceiver)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val data = parseData(intent)
        startForeground(ID, getNotification(data))

        registerReceiver(stopReceiver, IntentFilter(Recording.Action.STOP_RECORDING))
        locationService.startListen {
            viewModel.onLocationUpdate(it.toPoint())
        }
        return START_STICKY
    }

    private fun parseData(intent: Intent): RecordingData {
        val data = intent.getSerializableExtra(Recording.RECORDING)!!.castTo<RecordingParam>()
            .toRecordingData()
        viewModel.proceedRecordingData(data)
        return data
    }

    private fun getNotification(data: RecordingData): Notification {
        val title = when (data) {
            is RecordingData.Routing -> getString(R.string.routing_title)
            is RecordingData.Entrying -> getString(R.string.entrying_title)
        }

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground()
        } else {
            NotificationCompat.Builder(this)
        }
        return builder
            .setSmallIcon(R.drawable.ic_gps_marker)
            .setContentTitle(title)
            .setContentText(getString(R.string.notification_text))
            .setContentIntent(PendingIntent.getBroadcast(this, 0, Intent(Recording.Action.STOP_RECORDING), 0))
            .setNotificationSilent()
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground(): NotificationCompat.Builder {
        val channelName = getString(R.string.channel_name)
        val chan = NotificationChannel(
            CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)
        return NotificationCompat.Builder(this, CHANNEL_ID)
    }
}

sealed class RecordingData {

    data class Routing(
        val points: List<Point>
    ) : RecordingData()

    data class Entrying(
        val routeId: Int,
        val routeName: String?,
        val routePoints: List<Point>,
        val points: List<Point>
    ) : RecordingData()

    fun toRecordingParam(): RecordingParam {
        return when (this) {
            is Routing -> RecordingParam.Routing(
                points = points.map { it.toPointParam() }.toTypedArray()
            )
            is Entrying -> RecordingParam.Entrying(
                routeId = routeId,
                routeName = routeName,
                routePoints = routePoints.map { it.toPointParam() }.toTypedArray(),
                points = points.map { it.toPointParam() }.toTypedArray()
            )
        }
    }
}

sealed class RecordingParam : Serializable {

    data class Routing(
        val points: Array<PointParam>
    ) : RecordingParam()

    data class Entrying(
        val routeId: Int,
        val routeName: String?,
        val routePoints: Array<PointParam>,
        val points: Array<PointParam>
    ) : RecordingParam()

    fun toRecordingData(): RecordingData {
        return when (this) {
            is Routing -> RecordingData.Routing(
                points = points.map { it.toPoint() }
            )
            is Entrying -> RecordingData.Entrying(
                routeId = routeId,
                routeName = routeName,
                routePoints = routePoints.map { it.toPoint() },
                points = points.map { it.toPoint() }
            )
        }
    }
}

object Recording {

    const val RECORDING = "RECORDING"

    object Action {

        private const val PREFIX = "LETS_A_GO"

        const val START_RECORDING = "${PREFIX}_START_RECORDING"
        const val STOP_RECORDING = "${PREFIX}_STOP_RECORDING"
        const val RECOVER = "${PREFIX}_RECOVER"
        const val DISMISS_STRICT_TO_ROUTE = "${PREFIX}_DISMISS_STRICT_TO_ROUTE"
    }
}