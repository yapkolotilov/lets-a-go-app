package me.kolotilov.lets_a_go.ui.map

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.ui.PointParam
import me.kolotilov.lets_a_go.ui.requireExtras
import me.kolotilov.lets_a_go.ui.toPoint
import me.kolotilov.lets_a_go.ui.toPointParam
import me.kolotilov.lets_a_go.utils.castToOrNull
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit


class MapService : Service(), LocationListener {

    private companion object {

        private const val ID = 1337
        private const val CHANNEL_ID = "LETS_A_GO"
    }

    private lateinit var notification: Notification
    private var timerDisposable: Disposable? = null

    private lateinit var recordingType: Recording.Type
    private var recordedPoints: MutableList<Point> = mutableListOf()
    private var recordingTime: Long = 0

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Recording.Action.STOP_RECORDING) {
                val mapFragmentIntent = Intent(Recording.Action.RECOVER).apply {
                    putExtra(Recording.Extra.TYPE, recordingType)
                    putExtra(
                        Recording.Extra.POINTS,
                        recordedPoints.map { it.toPointParam() }.toTypedArray()
                    )
                    putExtra(Recording.Extra.TIME, recordingTime)
                }
                sendBroadcast(mapFragmentIntent)
                stop()
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Изначальные данные:
        val extras = intent.requireExtras()
        recordingType = extras.getSerializable(Recording.Extra.TYPE) as Recording.Type
        recordedPoints =
            extras.getSerializable(Recording.Extra.POINTS)?.castToOrNull<Array<PointParam>>()
                ?.map { it.toPoint() }?.toMutableList() ?: mutableListOf()
        val timeOffset = extras.getLong(Recording.Extra.TIME, 0)
        val startTime = DateTime.now().millis

        // Уведомления:
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground()
        } else {
            Notification.Builder(this)
        }
        notification = builder
            .setSmallIcon(R.drawable.ic_gps_marker)
            .setContentTitle(title())
            .setContentText(getString(R.string.notification_text))
            .build()
        startForeground(ID, notification)

        // Подписка:
        val client =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (checkPermission()) {
            client.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        }

        timerDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                recordingTime = DateTime.now().millis - startTime + timeOffset
            }
            .subscribe({}, {})
        registerReceiver(receiver, IntentFilter(Recording.Action.STOP_RECORDING))
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onLocationChanged(location: Location) {
        val point = location.toPoint()
        recordedPoints.add(point)
    }

    private fun stop() {
        timerDisposable?.dispose()
        stopSelf()
    }

    private fun title(): String {
        return when (recordingType) {
            Recording.Type.ROUTING -> getString(R.string.routing_title)
            Recording.Type.ENTRYING -> getString(R.string.entrying_title)
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground(): Notification.Builder {
        val channelId = "LETS_A_GO"
        val channelName = "Lets'a go!"
        val chan = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        return Notification.Builder(this, channelId)
    }
}

object Recording {

    object Extra {

        const val TYPE = "TYPE"
        const val POINTS = "POINTS"
        const val TIME = "TIME"
    }

    object Action {

        const val START_RECORDING = "LETS_A_GO_START_RECORDING"
        const val STOP_RECORDING = "LETS_A_GO_STOP_RECORDING"
        const val RECOVER = "LETS_A_GO_RECOVER"
    }

    enum class Type {

        ROUTING,

        ENTRYING
    }
}