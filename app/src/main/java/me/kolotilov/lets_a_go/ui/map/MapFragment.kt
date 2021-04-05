package me.kolotilov.lets_a_go.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.models.distance
import me.kolotilov.lets_a_go.presentation.map.MapViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import me.kolotilov.lets_a_go.ui.toLatLng
import me.kolotilov.lets_a_go.ui.toPoint
import org.joda.time.DateTime
import org.joda.time.Duration
import org.kodein.di.instance
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class MapFragment : BaseFragment(R.layout.fragment_map) {

    private companion object {

        const val LOCATION_REQUEST_CODE = 1
    }

    private abstract inner class State {

        abstract fun processLocation(location: Location)

        abstract fun onRecordClick()
    }

    private inner class Idle : State() {

        override fun processLocation(location: Location) {
            if (moved)
                return
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location.toLatLng(), 15F))
            moved = true
        }

        override fun onRecordClick() = startRecording()

        private fun startRecording() {
            state = Routing()
            recordPanel.isVisible = true
            recordButton.text = "Остановить"
            recordedPoints.clear()
            val startPoint = Point(
                latitude = currentPositionMarker.position.latitude,
                longitude = currentPositionMarker.position.longitude,
                timestamp = DateTime.now(),
                id = 0
            )
            recordedPoints.add(startPoint)

            timerDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    val duration = Duration(
                        recordedPoints.firstOrNull()?.timestamp ?: DateTime.now(),
                        DateTime.now()
                    )
                    updateRecordingPanel(duration.millis)
                }
                .subscribe({}, {})
        }
    }

    private inner class Routing : State() {

        override fun processLocation(location: Location) {
            val point = location.toPoint()
            recordedPoints.add(point)
            currentEntryPolyline.points = recordedPoints.map { LatLng(it.latitude, it.longitude) }
        }

        override fun onRecordClick() = stopRecording()

        private fun stopRecording() {
            state = Idle()
            recordPanel.isVisible = false
            recordButton.text = "Записать"
            updateRecordingPanel(0)
            timerDisposable?.dispose()
            viewModel.openEditRouteBottomSheet(recordedPoints) {
                currentEntryPolyline.remove()
            }
        }
    }

    private inner class Entrying : State() {

        override fun processLocation(location: Location) {
            val point = location.toPoint()
            recordedPoints.add(point)
        }

        override fun onRecordClick() {
            state = Idle()
            recordPanel.isVisible = false
            recordButton.text = "Записать"
            updateRecordingPanel(0)
            timerDisposable?.dispose()
            viewModel.openEditEntryBottomSheet(selectedRoute, recordedPoints) {}
        }
    }

    private val recordButton: Button by lazyView(R.id.record_button)
    private val recordPanel: View by lazyView(R.id.top_menu)
    private val durationTextView: TextView by lazyView(R.id.duration_TextView)
    private val distanceTextView: TextView by lazyView(R.id.length_TextView)
    private val userDetailsButton: Button by lazyView(R.id.user_details_button)
    override val viewModel: MapViewModel by instance()

    private var timerDisposable: Disposable? = null
    private val dateFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    private val recordedPoints = mutableListOf<Point>()
    private var routeMarkers = emptyList<Marker>()
    private var state: State = Idle()
    private lateinit var map: GoogleMap
    private var moved: Boolean = false
    private var selectedRoute: Route? = null
    private val currentPositionMarker by lazy {
        map.addMarker(
            MarkerOptions().title("Локация").position(LatLng(0.0, 0.0)).icon(
                bitmapDescriptorFromVector(requireContext(), R.drawable.ic_navigation)
            )
        )
    }
    private val currentEntryPolyline by lazy {
        map.addPolyline(PolylineOptions().addAll(emptyList()))
    }
    private var currentRoutePolyline: Polyline? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it
            onMapLoaded()
        }
    }

    override fun fillViews() {
        recordButton.setOnClickListener {
            onRecordClick()
        }
    }

    private fun onRecordClick() {
        state.onRecordClick()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates()
            }
        }
    }

    override fun bind() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestLocationUpdates()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        }
        userDetailsButton.setOnClickListener { viewModel.openUserDetails() }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val client = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        client.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5f) {
            onLocationUpdate(it)
        }
    }

    override fun subscribe() {
        viewModel.routes.subscribe { routes ->
            currentRoutePolyline?.remove()
            routeMarkers.forEach {
                it.remove()
            }
            routeMarkers = routes.map { route ->
                val point = route.points.first()
                map.addMarker(
                    MarkerOptions().position(LatLng(point.latitude, point.longitude))
                        .title(route.name)
                ).also { it.tag = route }
            }
        }.autoDispose()

        viewModel.startEntry.subscribe {
            state.onRecordClick()
            state = Entrying()
        }.autoDispose()
    }

    private fun onMarkerClick(marker: Marker) {
        val route = marker.tag as? Route ?: return
        selectedRoute = route
        drawRoute(route)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(route.points.first().toLatLng(), 15F),
            object : GoogleMap.CancelableCallback {
                override fun onFinish() {
                    viewModel.openRouteDetailsBottomSheet(route)
                }

                override fun onCancel() = Unit
            })
    }

    private fun drawRoute(route: Route) {
        routeMarkers.forEach { it.remove() }
        currentRoutePolyline?.remove()
        currentRoutePolyline = map.addPolyline(
            PolylineOptions().color(Color.BLUE).addAll(route.points.map { it.toLatLng() })
        )
    }

    private fun onLocationUpdate(location: Location) {
        currentPositionMarker.position = location.toLatLng()
        state.processLocation(location)
    }

    private fun updateRecordingPanel(duration: Long) {
        durationTextView.text = dateFormatter.format(duration - 3 * DateUtils.HOUR_IN_MILLIS)
        distanceTextView.text = "${recordedPoints.distance().roundToInt()} м."
    }

    private fun onMapLoaded() {
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.setOnMarkerClickListener {
            onMarkerClick(it)
            true
        }
    }

    private fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}