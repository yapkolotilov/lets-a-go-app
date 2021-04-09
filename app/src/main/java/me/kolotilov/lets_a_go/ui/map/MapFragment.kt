package me.kolotilov.lets_a_go.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
import me.kolotilov.lets_a_go.presentation.Tags
import me.kolotilov.lets_a_go.presentation.base.ButtonData
import me.kolotilov.lets_a_go.presentation.base.showDialog
import me.kolotilov.lets_a_go.presentation.map.MapViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import me.kolotilov.lets_a_go.ui.base.Grid
import me.kolotilov.lets_a_go.ui.base.KeyValueFactory
import me.kolotilov.lets_a_go.ui.base.KeyValueModel
import me.kolotilov.lets_a_go.ui.distance
import me.kolotilov.lets_a_go.ui.getColorCompat
import me.kolotilov.lets_a_go.ui.toLatLng
import me.kolotilov.lets_a_go.ui.toPoint
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormatter
import org.kodein.di.instance
import java.util.concurrent.TimeUnit


class MapFragment : BaseFragment(R.layout.fragment_map), LocationListener {

    private companion object {

        const val LOCATION_REQUEST_CODE = 1
    }

    private abstract inner class State {

        abstract fun processLocation(location: Location)

        abstract fun onRecordClick()
    }

    private inner class Idle : State() {

        private var moved: Boolean = false

        override fun processLocation(location: Location) {
            if (moved)
                return
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location.toLatLng(), 15F))
            moved = true
        }

        override fun onRecordClick() = startRecording()

        private fun startRecording() {
            state = routing
            setRecordPanelVisibility(true)
            recordedPoints.clear()
            currentPositionMarker.position
            val currentLocation = currentLocation
            if (currentLocation == null)
                return
            val startPoint = Point(
                latitude = currentLocation.latitude,
                longitude = currentLocation.longitude,
                altitude = currentLocation.altitude,
                timestamp = DateTime(currentLocation.time),
            )
            recordedPoints.add(startPoint)

            timerDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    val duration = Duration(
                        recordedPoints.firstOrNull()?.timestamp ?: DateTime.now(),
                        DateTime.now()
                    )
                    updateRecordingPanel(duration)
                }
                .emptySubscribe()
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
            state = idle
            setRecordPanelVisibility(false)
            updateRecordingPanel(Duration.ZERO)
            timerDisposable?.dispose()
            viewModel.openRoutePreview(recordedPoints)
        }
    }

    private inner class Entrying : State() {

        override fun processLocation(location: Location) {
            val point = location.toPoint()
            recordedPoints.add(point)
        }

        override fun onRecordClick() {
            state = idle
            setRecordPanelVisibility(false)
            updateRecordingPanel(Duration.ZERO)
            timerDisposable?.dispose()
            viewModel.openEditEntryBottomSheet(selectedRoute, recordedPoints) {}
        }
    }

    override val viewModel: MapViewModel by instance()

    private val recordButton: View by lazyView(R.id.record_button)
    private val recordPanel: View by lazyView(R.id.record_panel)
    private val recordGrid: GridLayout by lazyView(R.id.record_grid)
    private val searchButton: ImageButton by lazyView(R.id.search_button)
    private val userDetailsButton: ImageButton by lazyView(R.id.user_details_button)
    private lateinit var recordAdapter: Grid.ListAdapter

    private var timerDisposable: Disposable? = null
    private val dateFormatter: DateTimeFormatter by instance(Tags.RECORDING_TIME)

    private val recordedPoints = mutableListOf<Point>()
    private var routeMarkers = emptyList<Marker>()
    private val idle: State = Idle()
    private val routing: State = Routing()
    private val entrying: State = Entrying()
    private var state: State = idle
    private lateinit var map: GoogleMap
    private var selectedRoute: Route? = null
    private val currentPositionMarker by lazy {
        map.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_navigation))
        )
    }
    private val currentEntryPolyline by lazy {
        map.addPolyline(
            PolylineOptions()
                .color(requireContext().getColorCompat(R.color.red))
                .width(15f)
                .addAll(emptyList())
        )
    }
    private val currentRoutePolyline: Polyline by lazy {
        map.addPolyline(
            PolylineOptions()
                .color(Color.BLUE)
                .width(15f)
                .addAll(emptyList())
        )
    }
    private lateinit var client: LocationManager
    private var currentLocation: Location? = null

    override fun onDestroy() {
        super.onDestroy()
        client.removeUpdates(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates()
            } else {
                showDialog(
                    title = getString(R.string.give_permission_title),
                    message = getString(R.string.give_permission_message),
                    positiveButton = ButtonData(getString(R.string.ok_button))
                )
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        currentLocation = location
        recordButton.isClickable = true
        currentPositionMarker.position = location.toLatLng()
        state.processLocation(location)
    }

    override fun fillViews() {
        recordAdapter = Grid.ListAdapter(recordGrid, KeyValueFactory())
        animateLayoutChanges = true
        recordButton.setOnClickListener {
            state.onRecordClick()
        }
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it
            onMapLoaded()
        }
    }

    override fun bind() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestLocationUpdates()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }
        userDetailsButton.setOnClickListener { viewModel.openUserDetails() }
    }

    override fun subscribe() {
        viewModel.routes.subscribe { routes ->
            currentRoutePolyline.points = emptyList()
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
            state = entrying
        }.autoDispose()

        viewModel.errorDialog.subscribe {
            showDialog(
                title = getString(R.string.route_too_short_title),
                message = getString(R.string.route_too_short_message),
                positiveButton = ButtonData(getString(R.string.ok_button))
            )
        }.autoDispose()

        viewModel.clearEntry.subscribe {
            currentEntryPolyline.points = emptyList()
        }.autoDispose()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        client = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        client.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1f, this)
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
        currentRoutePolyline.points = route.points.map { it.toLatLng() }
    }

    private fun updateRecordingPanel(duration: Duration) {
        recordAdapter.items = listOf(
            KeyValueModel(getString(R.string.duration_hint), dateFormatter.print(duration.millis)),
            KeyValueModel(
                getString(R.string.distance_hint),
                recordedPoints.distance().distance(requireContext())
            )
        )
    }

    private fun onMapLoaded() {
        map.setOnMarkerClickListener {
            onMarkerClick(it)
            true
        }
    }

    @Suppress("SameParameterValue")
    private fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor {
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

    private fun setRecordPanelVisibility(visibility: Boolean) {
        val parent = ConstraintLayout.LayoutParams.PARENT_ID
        recordPanel.layoutParams = ConstraintLayout.LayoutParams(recordPanel.layoutParams).apply {
            if (visibility) {
                topToBottom = -1
                topToTop = parent
            } else {
                bottomToTop = parent
                topToTop = -1
            }
        }
    }
}