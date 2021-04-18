package me.kolotilov.lets_a_go.ui.map

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridLayout
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.ErrorCode
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.models.RoutePoint
import me.kolotilov.lets_a_go.models.distance
import me.kolotilov.lets_a_go.presentation.base.ButtonData
import me.kolotilov.lets_a_go.presentation.base.showDialog
import me.kolotilov.lets_a_go.presentation.details.UserDetailsResult
import me.kolotilov.lets_a_go.presentation.map.DynamicData
import me.kolotilov.lets_a_go.presentation.map.MapViewModel
import me.kolotilov.lets_a_go.presentation.map.StaticData
import me.kolotilov.lets_a_go.presentation.map.UserLocation
import me.kolotilov.lets_a_go.ui.*
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import me.kolotilov.lets_a_go.ui.base.Grid
import me.kolotilov.lets_a_go.ui.base.KeyValueFactory
import me.kolotilov.lets_a_go.ui.base.toKeyValueModel
import org.kodein.di.instance


class MapFragment : BaseFragment(R.layout.fragment_map) {

    companion object {
        private const val ROUTE_ID = "ROUTE_ID"
        private const val ENTRY_ID = "ENTRY_ID"

        private const val OVERVIEW_ZOOM = 15f
        private const val RECORDING_ZOOM = 17f
        private const val RECORDING_TILT = 60f
    }

    private inner class DialogHelper {

        var isAnimating: Boolean = false
            private set
        var isDialogShown: Boolean = false
            private set
        val errorRequests: MutableList<ErrorCode> = mutableListOf()
        val animationRequests: MutableList<() -> Unit> = mutableListOf()

        fun requestError(errorCode: ErrorCode) {
            if (isAnimating)
                errorRequests.add(errorCode)
            else
                parseErrorImpl(errorCode)
        }

        fun requestAnimation(
            cameraUpdate: CameraUpdate,
            callback: () -> Unit = {}
        ) {
            if (isDialogShown)
                animationRequests.add { animate(cameraUpdate, callback) }
            else
                animate(cameraUpdate, callback)
        }

        fun dialogShown() {
            isDialogShown = true
        }

        fun dialogHidden() {
            isDialogShown = false
            animationRequests.forEach {
                it()
            }
            animationRequests.clear()
        }

        fun animationStarted() {
            isAnimating = true
        }

        fun animationStopped() {
            isAnimating = false
            errorRequests.forEach {
                parseErrorImpl(it)
            }
            errorRequests.clear()
        }

        private fun animate(cameraUpdate: CameraUpdate,
                            callback: () -> Unit = {}) {
            dialogHelper.animationStarted()
            map.animateCamera(cameraUpdate, object : GoogleMap.CancelableCallback {

                override fun onFinish() {
                    dialogHelper.animationStopped()
                    callback()
                }

                override fun onCancel() {
                    dialogHelper.animationStopped()
                    callback()
                }
            })
        }
    }

    init {
        arguments = Bundle()
    }

    override val viewModel: MapViewModel by instance()
    private val locationService: LocationService by instance()

    private val filterSwitch: SwitchCompat by lazyView(R.id.filter_switch)
    private val recordButton: RecordButton by lazyView(R.id.record_button)
    private val searchButton: View by lazyView(R.id.search_button)
    private val userDetailsButton: View by lazyView(R.id.user_details_button)
    private val recordingPanel: View by lazyView(R.id.record_panel)
    private val recordGrid: GridLayout by lazyView(R.id.record_grid)

    private val recordAdapter: Grid.ListAdapter by lazyProperty {
        Grid.ListAdapter(recordGrid, KeyValueFactory())
    }

    private lateinit var map: GoogleMap
    private val locationMarker by lazyProperty {
        Log.d("BRUH", "locationMarker()")
        map.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .icon(requireContext().bitmapDescriptorFromVector(R.drawable.ic_navigation))
        )
    }
    private val routePolyline: Polyline by lazyProperty {
        map.addPolyline(
            PolylineOptions()
                .color(requireContext().getColorCompat(R.color.blue_primary))
                .width(15f)
                .addAll(emptyList())
        )
    }
    private val routesClusterManager: ClusterManager<RouteMarker> by lazyProperty {
        ClusterManager(requireContext(), map)
    }

    private val dialogHelper = DialogHelper()
    private var userRotation: Float = 0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        locationMarker.remove()
        routePolyline.remove()
        routesClusterManager.clearItems()
        routesClusterManager.cluster()
        super.onDestroyView()
    }

    override fun fillViews() {
        animateLayoutChanges = true
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it
            onMapLoaded()
        }
    }

    override fun bind() {
        recordButton.setOnClickListener {
            viewModel.onRecordClick()
        }
        searchButton.setOnClickListener {
            viewModel.openSearch()
        }
        userDetailsButton.setOnClickListener {
            viewModel.openUserDetails()
        }
        filterSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setFilterMap(isChecked)
        }
    }

    override fun subscribe() {
        viewModel.errorDialog.subscribe {
            parseError(it)
        }.autoDispose()

        viewModel.filterMap.subscribe {
            filterSwitch.isChecked = it
        }.autoDispose()

        viewModel.dynamicData.subscribe {
            parseDynamicData(it)
        }.autoDispose()

        viewModel.staticData.subscribe {
            parseStaticData(it)
        }.autoDispose()

        viewModel.camLocation.subscribe {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(it.toLatLng(), OVERVIEW_ZOOM))
        }.autoDispose()

        viewModel.userDetails.subscribe {
            parseUserDetailsResult(it)
        }.disposeOnDestroy()
    }

    private fun parseDynamicData(data: DynamicData) {
        updateLocation(data.location)
        setRecordPanelVisibility(data is DynamicData.Routing || data is DynamicData.Entrying)

        if (data is DynamicData.Routing || data is DynamicData.Entrying) {
            val cameraPosition = CameraPosition.Builder(map.cameraPosition)
                .target(data.location.toLatLng())
                .bearing(data.location.bearing.toFloat())
                .tilt(RECORDING_TILT)
                .zoom(RECORDING_ZOOM)
                .build()
            val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
            map.smartAnimateCamera(cameraUpdate)
        }

        when (data) {
            is DynamicData.Idle -> Unit
            is DynamicData.Routing -> {
                recordAdapter.items = listOf(
                    getString(R.string.duration_hint) to data.duration.duration(),
                    getString(R.string.distance_hint) to data.distance.distance(requireContext())
                ).map { it.toKeyValueModel() }
                routePolyline.points = data.points.map { it.toLatLng() }
            }
            is DynamicData.Entrying -> {
                recordAdapter.items = listOf(
                    getString(R.string.route_hint) to data.routeName,
                    getString(R.string.duration_hint) to data.duration.duration(),
                    getString(R.string.distance_hint) to data.distance.distance(requireContext())
                ).filter { it.second != null }.map { it.toKeyValueModel() }
            }
        }
    }

    private fun parseStaticData(data: StaticData) {
        setRecordPanelVisibility(data is StaticData.Routing || data is StaticData.Entrying)

        when (data) {
            is StaticData.Routing, is StaticData.Entrying -> {
                val cameraPosition = CameraPosition.Builder()
                    .target(locationMarker.position)
                    .bearing(locationMarker.rotation)
                    .tilt(RECORDING_TILT)
                    .zoom(RECORDING_ZOOM)
                    .build()
                val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
                map.smartAnimateCamera(cameraUpdate, force = true)
            }
            is StaticData.Idle -> {
                val cameraPosition = CameraPosition.Builder()
                    .target(map.cameraPosition.target)
                    .bearing(0f)
                    .tilt(0f)
                    .zoom(OVERVIEW_ZOOM)
                    .build()
                val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
                map.smartAnimateCamera(cameraUpdate, force = true)
            }
            else -> Unit
        }

        when (data) {
            is StaticData.Idle -> {
                drawRoutes(data.routes)
            }
            is StaticData.Routing -> {
                clearMap()
            }
            is StaticData.Entrying -> {
                drawRoute(data.points)
            }
            is StaticData.RoutePreview -> {
                drawRoute(data.points)
                if (data.points.distance() >= 100f) {
                    centerCamera(data.points.map { it.toLatLng() }, force = true) {
                        viewModel.openRoutePreview(data.points)
                    }
                } else {
                    viewModel.openRoutePreview(data.points)
                }
            }
            is StaticData.EntryPreview -> {
                drawRoute(data.points)
                centerCamera(data.points.map { it.toLatLng() }, force = true) {
                    viewModel.openEntryPreview(data.preview, data.points)
                }
            }
            is StaticData.RouteDetails -> {
                drawRoute(data.points)
                centerCamera(data.points.map { it.toLatLng() }, force = true) {
                    viewModel.openRouteDetails(data.id)
                }
            }
        }
    }

    private fun updateLocation(location: UserLocation) {
        userRotation = location.bearing.toFloat()
        locationMarker.position = location.toLatLng()
        locationMarker.rotation = calculateUserRotation()
    }

    private fun centerCamera(
        points: List<LatLng>,
        force: Boolean = true,
        callback: () -> Unit = {}
    ) {
        if (points.isNotEmpty()) {
            val builder = LatLngBounds.builder()
            for (item in points) {
                builder.include(item)
            }
            val bounds = builder.build()
            val padding = 100.dp(requireContext())
            map.smartAnimateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding), force) {
                callback()
            }
        } else {
            callback()
        }
    }

    private fun parseError(it: ErrorCode) {
        Log.d("BRUH", "error")
        dialogHelper.requestError(it)
    }

    private fun parseErrorImpl(it: ErrorCode) {
        val okButton = ButtonData(getString(R.string.ok_button)) {
            dialogHelper.dialogHidden()
        }
        when (it) {
            ErrorCode.ENTRY_TOO_SHORT -> {
                dialogHelper.dialogShown()
                showDialog(
                    title = getString(R.string.route_too_short_title),
                    message = getString(R.string.route_too_short_message),
                    positiveButton = okButton
                )
            }
            ErrorCode.SPEED_TOO_FAST -> {
                dialogHelper.dialogShown()
                showDialog(
                    title = getString(R.string.speed_too_fast_title),
                    message = getString(R.string.speed_too_fast_message),
                    positiveButton = okButton
                )
            }
            else -> Unit
        }
    }

    private fun parseUserDetailsResult(userDetailsResult: UserDetailsResult) {
        when (userDetailsResult) {
            is UserDetailsResult.Entry -> {
                arguments = Bundle().apply {
                    putInt(ROUTE_ID, userDetailsResult.routeId)
                    putInt(ENTRY_ID, userDetailsResult.id)
                }
            }
            is UserDetailsResult.Route -> {
                arguments = Bundle().apply {
                    putInt(ROUTE_ID, userDetailsResult.id)
                }
            }
        }
    }

    private fun clearMap() {
        routesClusterManager.clearItems()
        routesClusterManager.cluster()
        routePolyline.points = emptyList()
    }

    private fun drawRoute(route: List<Point>) {
        clearMap()
        routePolyline.points = route.map { it.toLatLng() }
    }

    private fun drawRoutes(routes: List<RoutePoint>) {
        clearMap()
        routesClusterManager.addItems(routes.map { RouteMarker(it) })
        routesClusterManager.cluster()
    }

    private fun onMapLoaded() {
        recordButton.isClickable = true
        locationService.startListen {
            viewModel.onLocationUpdate(it.toPoint())
        }
        map.setOnCameraIdleListener(routesClusterManager)
        map.setOnCameraMoveListener {
            locationMarker.rotation = calculateUserRotation()
        }
        routesClusterManager.renderer = RouteRenderer(requireContext(), map, routesClusterManager)
        routesClusterManager.setOnClusterItemClickListener {
            viewModel.showRoute(it.route.id)
            true
        }
        routesClusterManager.setOnClusterClickListener {
            centerCamera(it.items.map { it.position })
            true
        }

        viewModel.onMapLoaded()
        readArguments()
    }

    private fun readArguments() {
        requireArguments().takeIf { !it.isEmpty }?.apply {
            val routeId = getInt(ROUTE_ID, -1)
            val entryId = getInt(ENTRY_ID, -1).takeIf { it >= 0 }
            viewModel.proceedArguments(entryId, routeId)
        }
        arguments = Bundle()
    }

    private fun calculateUserRotation(): Float {
        val cameraRotation = map.cameraPosition.bearing
        return (userRotation + 360 - cameraRotation).rem(360)
    }

    private fun GoogleMap.smartAnimateCamera(
        cameraUpdate: CameraUpdate,
        force: Boolean = false,
        callback: () -> Unit = {}
    ) {
        if (dialogHelper.isAnimating && !force) {
            callback()
            return
        }
        dialogHelper.requestAnimation(cameraUpdate, callback)
    }

    private fun setRecordPanelVisibility(visibility: Boolean) {
        val parent = ConstraintLayout.LayoutParams.PARENT_ID
        recordingPanel.layoutParams =
            ConstraintLayout.LayoutParams(recordingPanel.layoutParams).apply {
                if (visibility) {
                    topToBottom = -1
                    topToTop = parent
                } else {
                    bottomToTop = parent
                    topToTop = -1
                }
            }
        filterSwitch.isVisible = !visibility
    }
}