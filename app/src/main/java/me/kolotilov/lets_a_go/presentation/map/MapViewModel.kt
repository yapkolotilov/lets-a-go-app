package me.kolotilov.lets_a_go.presentation.map

import com.google.android.gms.location.LocationRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.models.*
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Constants
import me.kolotilov.lets_a_go.presentation.Results
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.presentation.base.NotificationService
import me.kolotilov.lets_a_go.presentation.base.RecordingType
import me.kolotilov.lets_a_go.presentation.details.UserDetailsResult
import me.kolotilov.lets_a_go.ui.base.setResultListener
import me.kolotilov.lets_a_go.ui.map.RecordingData
import me.kolotilov.lets_a_go.ui.toEditRouteParams
import me.kolotilov.lets_a_go.utils.copy
import org.joda.time.DateTime
import org.joda.time.Duration
import ru.terrakok.cicerone.Router
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class MapViewModel(
    private val repository: Repository,
    private val router: Router,
    private val notificationService: NotificationService
) : BaseViewModel() {

    private abstract inner class State {

        open fun start() = Unit

        abstract fun onLocationUpdate(location: Point)

        abstract fun onRecordClick(location: Point)

        open fun stop() = Unit
    }

    private inner class Idle : State() {

        override fun onLocationUpdate(location: Point) {
            if (!locationUpdated) {
                camLocationSubject.onNext(location)
                locationUpdated = true
            }
            dynamicDataSubject.onNext(
                DynamicData.Idle(
                    location = location.toUserLocation(),
                )
            )
        }

        override fun onRecordClick(location: Point) {
            state = Routing()
            staticDataSubject.onNext(StaticData.Routing(bearing()))
        }
    }

    private inner class Routing : State() {

        val recordedPoints: MutableList<Point> = mutableListOf()
        private var timerDisposable: Disposable? = null

        override fun start() {
            notificationService.showRecordingNotification(RecordingType.ROUTING)
            recordedPoints.addDistinct(currentLocation!!.copy(timestamp = DateTime.now()))
            timerDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    dynamicDataSubject.onNext(
                        DynamicData.Routing(
                            location = currentLocation!!.toUserLocation(),
                            points = recordedPoints,
                            duration = recordedPoints.durationTillNow(),
                            distance = recordedPoints.distance()
                        )
                    )
                }
                .emptySubscribe()
        }

        override fun onLocationUpdate(location: Point) {
            recordedPoints.addDistinct(location)
            dynamicDataSubject.onNext(
                DynamicData.Routing(
                    location = location.toUserLocation(),
                    points = recordedPoints,
                    duration = recordedPoints.durationTillNow(),
                    distance = recordedPoints.distance(),
                )
            )
        }

        override fun onRecordClick(location: Point) {
            val recordedPoints = recordedPoints.copy()
            staticDataSubject.onNext(
                StaticData.RoutePreview(
                    points = recordedPoints
                )
            )
            state = Idle()
        }

        override fun stop() {
            timerDisposable?.dispose()
            notificationService.hideRecordingNotification()
            notificationService.hideStickToRouteNotification()
        }
    }

    private inner class Entrying(
        val id: Int,
        val name: String?,
        val points: List<Point>
    ) : State() {

        val recordedPoints: MutableList<Point> = mutableListOf()
        private var timerDisposable: Disposable? = null

        override fun start() {
            notificationService.showRecordingNotification(RecordingType.ENTRYING)
            LocationRequest.create()
            recordedPoints.addDistinct(currentLocation!!.copy(timestamp = DateTime.now()))
            timerDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    dynamicDataSubject.onNext(
                        DynamicData.Entrying(
                            location = currentLocation!!.toUserLocation(),
                            routeName = name,
                            duration = recordedPoints.durationTillNow(),
                            distance = recordedPoints.distance()
                        )
                    )
                }
                .emptySubscribe()
        }

        override fun onLocationUpdate(location: Point) {
            recordedPoints.addDistinct(location)
            dynamicDataSubject.onNext(
                DynamicData.Entrying(
                    location = location.toUserLocation(),
                    routeName = name,
                    duration = recordedPoints.durationTillNow(),
                    distance = recordedPoints.distance(),
                )
            )
            val distance = points.minOf { it distance location }
            if (distance > Constants.MIN_DISTANCE_TO_ROUTE)
                notificationService.showStickToRouteNotification()
            else
                notificationService.hideStickToRouteNotification()
        }

        override fun onRecordClick(location: Point) {
            val recordedPoints = recordedPoints.copy()
            repository.entryPreview(id, recordedPoints)
                .load()
                .doOnSuccess {
                    staticDataSubject.onNext(
                        StaticData.EntryPreview(
                            preview = it,
                            points = points
                        )
                    )
                }
                .doOnError {
                    loadRoutes()
                }
                .emptySubscribe()
                .autoDispose()
            state = Idle()
        }

        override fun stop() {
            timerDisposable?.dispose()
            notificationService.hideRecordingNotification()
            notificationService.hideStickToRouteNotification()
        }
    }

    /**
     * Данные для карты.
     */
    val dynamicData: Observable<DynamicData> get() = dynamicDataSubject
        .distinctUntilChanged { a, b -> a.location == b.location && a.duration == b.duration }
    private val dynamicDataSubject: Subject<DynamicData> = BehaviorSubject.create()

    /**
     * Данные для интерфейса.
     */
    val staticData: Observable<StaticData> get() = staticDataSubject
    private val staticDataSubject: Subject<StaticData> = BehaviorSubject.create()

    val userDetails: Observable<UserDetailsResult> get() = userDetailsSubject
    private val userDetailsSubject: Subject<UserDetailsResult> = PublishSubject.create()

    /**
     * Включён ли фильтр.
     */
    val filterMap: Observable<Boolean> get() = filterMapSubject
    private val filterMapSubject: Subject<Boolean> = BehaviorSubject.create()

    val camLocation: Observable<Point> get() = camLocationSubject
    private val camLocationSubject: Subject<Point> = PublishSubject.create()

    val isRecording: Observable<Boolean> get() = isRecordingSubject
    private val isRecordingSubject: Subject<Boolean> = BehaviorSubject.create()

    private var state: State = Idle()
        set(value) {
            if (field === value)
                return
            field.stop()
            field = value
            value.start()
            isRecordingSubject.onNext(value !is Idle)
        }
    private var previousLocation: Point? = null
    private var currentLocation: Point? = null
    private var isInitialized: Boolean = false
    private var bearing: Double = 0.0
    private var locationUpdated: Boolean = false

    fun proceedArguments(entryId: Int?, routeId: Int) {
        showRouteImpl(routeId, entryId)
    }

    fun onMapLoaded() {
        router.setResultListener<EntryPreviewResult>(Results.ENTRY_PREVIEW) {
            when (it) {
                is EntryPreviewResult.LoadRoutes -> {
                    loadRoutes()
                }
                is EntryPreviewResult.DoNothing -> Unit
            }
        }
        router.setResultListener<EditRouteResult>(Results.EDIT_ROUTE) {
            when (it) {
                is EditRouteResult.NewRoute -> Unit
                is EditRouteResult.Edited -> {
                    loadRoutes()
                }
                is EditRouteResult.Nothing -> Unit
            }
        }
        router.setResultListener<RouteDetailsResult>(Results.ROUTE_DETAILS) {
            when (it) {
                is RouteDetailsResult.LoadRoutes -> loadRoutes()
                is RouteDetailsResult.StartEntry -> {
                    repository.showStickToRoute = true
                    state = Entrying(it.id, it.name, it.points)
                }
            }
        }
        router.setResultListener<UserDetailsResult>(Results.USER_DETAILS) {
            userDetailsSubject.onNext(it)
        }

        if (!isInitialized) {
            val lastLocation = repository.lastLocation
            if (lastLocation != null) {
                onLocationUpdateImpl(lastLocation, notify = true)
                camLocationSubject.onNext(lastLocation)
            }
            filterMapSubject.onNext(repository.filterMap)
            loadRoutes()
        }
    }

    fun getRecordingData(): RecordingData? {
        state.stop()
        return when (val state = state) {
            is Routing -> RecordingData.Routing(
                points = state.recordedPoints.copy()
            )
            is Entrying -> RecordingData.Entrying(
                routeId = state.id,
                routeName = state.name,
                routePoints = state.points,
                points = state.recordedPoints.copy()
            )
            else -> null
        }
    }

    fun proceedRecordingData(data: RecordingData) {
        when (data) {
            is RecordingData.Routing -> {
                state = Routing().apply {
                    recordedPoints.addAll(data.points)
                }
                staticDataSubject.onNext(StaticData.Routing(
                    bearing = bearing()
                ))
            }
            is RecordingData.Entrying -> {
                state = Entrying(
                    id = data.routeId,
                    name = data.routeName,
                    points = data.routePoints
                ).apply {
                    recordedPoints.addAll(data.points)
                }
                staticDataSubject.onNext(StaticData.Entrying(
                    bearing = bearing(),
                    points = data.routePoints
                ))
            }
        }
    }

    fun setFilterMap(filterMap: Boolean) {
        repository.filterMap = filterMap
        loadRoutes()
    }

    fun onLocationUpdate(location: Point) {
        onLocationUpdateImpl(location, true)
    }

    fun setBearing(bearing: Double) {
        this.bearing = bearing
        val location = currentLocation ?: return
        if (state !is Idle)
            state.onLocationUpdate(location)
    }

    fun openEntryPreview(entryPreview: EntryPreview, points: List<Point>) {
        router.navigateTo(Screens.editEntry(entryPreview = entryPreview, points = points))
    }

    private fun onLocationUpdateImpl(location: Point, notify: Boolean = false) {
        repository.lastLocation = location
        if (currentLocation?.same(location) != true) {
            previousLocation = currentLocation
            currentLocation = location
        }
        if (notify)
            state.onLocationUpdate(currentLocation!!)
    }

    fun onRecordClick() {
        state.onRecordClick(currentLocation!!)
    }

    fun openSearch() {
        router.navigateTo(Screens.searchRoutes())
    }

    fun openUserDetails() {
        router.navigateTo(Screens.userDetails())
    }

    fun showRoute(routeId: Int) {
        showRouteImpl(routeId, null)
    }

    private fun showRouteImpl(routeId: Int, entryId: Int?) {
        repository.getRouteOnMap(routeId)
            .load()
            .doOnSuccess {
                staticDataSubject.onNext(
                    StaticData.RouteDetails(
                        id = it.id,
                        startPoint = RoutePoint(
                            type = it.type,
                            startPoint = it.points.firstOrNull() ?: Point.default(),
                            id = it.id
                        ),
                        points = it.points,
                        entryId = entryId
                    )
                )
            }
            .emptySubscribe()
            .autoDispose()
    }

    fun openRouteDetails(routeId: Int) {
        router.navigateTo(Screens.routeDetails(routeId))
    }

    fun openRoutePreview(recordedPoints: List<Point>) {
        repository.routePreview(recordedPoints)
            .load()
            .doOnSuccess {
                router.navigateTo(Screens.editRoute(it.toEditRouteParams(recordedPoints)))
            }
            .doOnError {
                loadRoutes()
            }
            .emptySubscribe()
            .autoDispose()
    }


    private fun loadRoutes() {
        repository.getAllRoutes(repository.filterMap)
            .load()
            .doOnSuccess {
                isInitialized = true
                staticDataSubject.onNext(
                    StaticData.Idle(
                        routes = it
                    )
                )
            }
            .emptySubscribe()
            .autoDispose()
    }

    fun bearing(): Double {
        if (state is Idle) return bearing
        val previousLocation = previousLocation
        val currentLocation = currentLocation
        if (previousLocation == null || currentLocation == null)
            return 0.0
        val lat1 = previousLocation.latitude * PI / 180
        val long1 = previousLocation.longitude * PI / 180
        val lat2 = currentLocation.latitude * PI / 180
        val long2 = currentLocation.longitude * PI / 180
        val dLon = long2 - long1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - (sin(lat1) * cos(lat2) * cos(dLon))
        var brng = atan2(y, x)
        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360
        return brng
    }

    private fun Point.toUserLocation() = UserLocation(
        latitude = latitude,
        longitude = longitude,
        bearing = bearing()
    )

    private fun <T> MutableList<T>.addDistinct(item: T) {
        if (lastOrNull() != item)
            add(item)
    }

    fun disableStrictToRoute() {
        repository.showStickToRoute = false
    }
}

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val bearing: Double
)

sealed class DynamicData {

    abstract val location: UserLocation
    open val duration: Duration? = null

    data class Idle(
        override val location: UserLocation,
    ) : DynamicData()

    data class Routing(
        override val location: UserLocation,
        val points: List<Point>,
        override val duration: Duration,
        val distance: Double,
    ) : DynamicData()

    data class Entrying(
        override val location: UserLocation,
        val routeName: String?,
        override val duration: Duration,
        val distance: Double,
    ) : DynamicData()
}

sealed class StaticData {

    data class Idle(
        val routes: List<RouteLine>
    ) : StaticData()

    class Routing(
        val bearing: Double
    ) : StaticData()

    data class Entrying(
        val bearing: Double,
        val points: List<Point>
    ) : StaticData()

    data class RouteDetails(
        val id: Int,
        val startPoint: RoutePoint?,
        val points: List<Point>,
        val entryId: Int?
    ) : StaticData()

    data class RoutePreview(
        val points: List<Point>
    ) : StaticData()

    data class EntryPreview(
        val preview: me.kolotilov.lets_a_go.models.EntryPreview,
        val points: List<Point>
    ) : StaticData()
}

fun List<Point>.speed(): Double {
    return (distance() / 1000) / (duration().millis.toDouble() / (60 * 60 * 1000))
}