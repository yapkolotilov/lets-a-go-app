package me.kolotilov.lets_a_go.ui.map

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.ui.PointParam
import me.kolotilov.lets_a_go.ui.base.BaseService
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
                MapFragment.start(context, viewModel.getRecordingData())
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

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


//    private lateinit var notification: Notification
//    private var timerDisposable: Disposable? = null
//
//    private lateinit var recordingType: Recording.Type
//    private var recordedPoints: MutableList<Point> = mutableListOf()
//    private var recordingTime: Long = 0
//
//    private val receiver = object : BroadcastReceiver() {
//
//        override fun onReceive(context: Context, intent: Intent) {
//            if (intent.action == Recording.Action.STOP_RECORDING) {
//                val mapFragmentIntent = Intent(Recording.Action.RECOVER).apply {
//                    val data = RecordingData(
//                        type = recordingType,
//                        points = recordedPoints
//                    )
//                    putExtra(Recording.RECORDING, data.toRecordingParam())
//                }
//                sendBroadcast(mapFragmentIntent)
//                stop()
//            }
//        }
//    }
//
//    override fun onBind(intent: Intent): IBinder? {
//        return null
//    }
//
//    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//        // Изначальные данные:
//        val data = intent.getSerializableExtra(Recording.RECORDING)!!.castTo<RecordingParam>().toRecordingData()
//        recordingType = data.type
//        recordedPoints = data.points.toMutableList()
//        val startTime = DateTime.now().millis
//
//        // Уведомления:
//        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startMyOwnForeground()
//        } else {
//            NotificationCompat.Builder(this)
//        }
//        notification = builder
//            .setSmallIcon(R.drawable.ic_gps_marker)
//            .setContentTitle(title())
//            .setContentText(getString(R.string.notification_text))
//            .build()
//        startForeground(ID, notification)
//
//        // Подписка:
//        val client =
//            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        if (checkPermission()) {
//            client.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
//        }
//
//        timerDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnNext {
//                recordingTime = DateTime.now().millis - startTime
//            }
//            .subscribe({}, {})
//        registerReceiver(receiver, IntentFilter(Recording.Action.STOP_RECORDING))
//        return START_STICKY
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(receiver)
//    }
//
//    override fun onLocationChanged(location: Location) {
//        val point = location.toPoint()
//        recordedPoints.add(point)
//    }
//
//    private fun stop() {
//        timerDisposable?.dispose()
//        stopSelf()
//    }
//
//    private fun title(): String {
//        return when (recordingType) {
//            Recording.Type.ROUTING -> getString(R.string.routing_title)
//            Recording.Type.ENTRYING -> getString(R.string.entrying_title)
//        }
//    }
//
//    private fun checkPermission(): Boolean {
//        return ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun startMyOwnForeground(): NotificationCompat.Builder {
//        val channelName = getString(R.string.channel_name)
//        val chan = NotificationChannel(
//            CHANNEL_ID,
//            channelName,
//            NotificationManager.IMPORTANCE_HIGH
//        )
//        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
//        manager.createNotificationChannel(chan)
//        return NotificationCompat.Builder(this, CHANNEL_ID)
//    }
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
            is RecordingParam.Routing -> RecordingData.Routing(
                points = points.map { it.toPoint() }
            )
            is RecordingParam.Entrying -> RecordingData.Entrying(
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
    }
}