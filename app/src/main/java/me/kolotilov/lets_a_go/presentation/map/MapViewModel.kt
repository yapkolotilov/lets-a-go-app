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
import me.kolotilov.lets_a_go.presentation.Results
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.presentation.details.UserDetailsResult
import me.kolotilov.lets_a_go.ui.base.setResultListener
import me.kolotilov.lets_a_go.ui.map.RecordingData
import me.kolotilov.lets_a_go.ui.toEditRouteParams
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
    private val router: Router
) : BaseViewModel() {

    private abstract inner class State {

        open fun start() = Unit

        abstract fun onLocationUpdate(location: Point)

        abstract fun onRecordClick(location: Point)

        open fun stop() = Unit
    }

    private inner class Idle : State() {

        override fun onLocationUpdate(location: Point) {
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

        private var timerDisposable: Disposable? = null

        override fun start() {
            recordedPoints.add(currentLocation!!.copy(timestamp = DateTime.now()))
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
            recordedPoints.add(location)
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
            state = Idle()
            val recordedPoints = recordedPoints.toList()
            staticDataSubject.onNext(
                StaticData.RoutePreview(
                    points = recordedPoints
                )
            )
            this@MapViewModel.recordedPoints.clear()
        }

        override fun stop() {
            timerDisposable?.dispose()
        }
    }

    private inner class Entrying(
        val id: Int,
        val name: String?,
        val points: List<Point>
    ) : State() {

        private var timerDisposable: Disposable? = null

        override fun start() {
            LocationRequest.create()
            recordedPoints.add(currentLocation!!.copy(timestamp = DateTime.now()))
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
            recordedPoints.add(location)
            dynamicDataSubject.onNext(
                DynamicData.Entrying(
                    location = location.toUserLocation(),
                    routeName = name,
                    duration = recordedPoints.durationTillNow(),
                    distance = recordedPoints.distance(),
                )
            )
        }

        override fun onRecordClick(location: Point) {
            state = Idle()
            val recordedPoints = recordedPoints.toList()
            repository.entryPreview(id, recordedPoints)
                .load()
                .doOnSuccess {
                    staticDataSubject.onNext(
                        StaticData.EntryPreview(
                            preview = it,
                            points = points
                        )
                    )
                    this@MapViewModel.recordedPoints.clear()
                }
                .emptySubscribe()
                .autoDispose()
        }

        override fun stop() {
            timerDisposable?.dispose()
        }
    }

    /**
     * Данные для карты.
     */
    val dynamicData: Observable<DynamicData> get() = dynamicDataSubject
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
    private var recordedPoints: MutableList<Point> = mutableListOf()
    private var isInitialized: Boolean = false
    private var bearing: Double = 0.0

    fun proceedArguments(entryId: Int?, routeId: Int) {
        showRouteImpl(routeId, entryId)
    }

    fun onMapLoaded() {
        router.setResultListener<Unit>(Results.EDIT_ROUTE) {
            loadRoutes()
        }
        router.setResultListener<RouteDetailsResult>(Results.ROUTE_DETAILS) {
            when (it) {
                is RouteDetailsResult.LoadRoutes -> loadRoutes()
                is RouteDetailsResult.StartEntry -> {
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
                onLocationUpdateImpl(lastLocation, false)
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
                points = recordedPoints
            )
            is Entrying -> RecordingData.Entrying(
                routeId = state.id,
                routeName = state.name,
                routePoints = state.points,
                points = recordedPoints
            )
            else -> null
        }
    }

    fun proceedRecordingData(data: RecordingData) {
        when (data) {
            is RecordingData.Routing -> {
                recordedPoints = data.points.toMutableList()
                state = Routing()
                staticDataSubject.onNext(StaticData.Routing(
                    bearing = bearing()
                ))
            }
            is RecordingData.Entrying -> {
                recordedPoints = data.points.toMutableList()
                state = Entrying(
                    id = data.routeId,
                    name = data.routeName,
                    points = data.routePoints
                )
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

    fun onLocationUpdate(location: Point, bearing: Double) {
        this.bearing = bearing
        onLocationUpdateImpl(location, true)
    }

    fun openEntryPreview(entryPreview: EntryPreview, points: List<Point>) {
        router.navigateTo(Screens.editEntry(entryPreview = entryPreview, points = points))
    }

    private fun onLocationUpdateImpl(location: Point, notify: Boolean = false) {
        if (currentLocation?.same(location) != true) {
            previousLocation = currentLocation
            currentLocation = location
            repository.lastLocation = location
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

    private fun bearing(): Double {
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
        bearing = bearing
    )
}

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val bearing: Double
)

sealed class DynamicData {

    abstract val location: UserLocation

    data class Idle(
        override val location: UserLocation,
    ) : DynamicData()

    data class Routing(
        override val location: UserLocation,
        val points: List<Point>,
        val duration: Duration,
        val distance: Double,
    ) : DynamicData()

    data class Entrying(
        override val location: UserLocation,
        val routeName: String?,
        val duration: Duration,
        val distance: Double,
    ) : DynamicData()
}

sealed class StaticData {

    data class Idle(
        val routes: List<RoutePoint>
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