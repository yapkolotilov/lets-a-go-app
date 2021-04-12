package me.kolotilov.lets_a_go.presentation.map

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.models.RoutePoint
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Results
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.ui.base.setResultListener
import me.kolotilov.lets_a_go.ui.toEditRouteParams
import ru.terrakok.cicerone.Router

class MapViewModel(
    private val repository: Repository,
    private val router: Router
) : BaseViewModel() {

    val routes: Observable<List<RoutePoint>> get() = routesSubject
    private val routesSubject: Subject<List<RoutePoint>> = BehaviorSubject.create()

    val startEntry: Observable<Unit> get() = startEntrySubject
    private val startEntrySubject = PublishSubject.create<Unit>()

    val clearEntry: Observable<Unit> get() = clearEntrySubject
    private val clearEntrySubject: Subject<Unit> = PublishSubject.create()

    val drawRoute: Observable<List<Point>> get() = drawRouteSubject
    private val drawRouteSubject: Subject<List<Point>> = BehaviorSubject.create()

    val lastLocation: Observable<Point> get() = lastLocationSubject
    private val lastLocationSubject: Subject<Point> = BehaviorSubject.create()

    override fun attach() {
        loadRoutes()
        router.setResultListener<Boolean>(Results.LOAD_ROUTES) {
            clearEntrySubject.onNext(Unit)
            if (!it) {
                loadRoutes()
            } else {
                startEntrySubject.onNext(Unit)
            }
        }
    }

    fun setLastLocation(point: Point) {
        repository.lastLocation = point
    }

    fun requestLastLocation() {
        val lastLocation = repository.lastLocation
        if (lastLocation != null) {
            lastLocationSubject.onNext(lastLocation)
        }
    }

    fun openRoutePreview(recordedPoints: MutableList<Point>) {
        repository.routePreview(recordedPoints)
            .load()
            .doOnSuccess {
                router.navigateTo(Screens.editRoute(it.toEditRouteParams(recordedPoints)))
            }
            .emptySubscribe()
            .autoDispose()
    }

    fun openEntryPreview(routeId: Int, points: List<Point>) {
        repository.entryPreview(routeId, points)
            .load()
            .doOnSuccess {
                router.navigateTo(Screens.editEntry(it, points))
            }
            .emptySubscribe()
            .autoDispose()
    }

    fun openRouteDetailsBottomSheet(routeId: Int) {
        router.navigateTo(Screens.routeDetails(routeId))
    }

    fun drawRoute(id: Int) {
        repository.getRouteOnMap(id)
            .load()
            .doOnSuccess {
                drawRouteSubject.onNext(it.points)
            }
            .emptySubscribe()
            .autoDispose()
    }

    fun loadRoutes() {
        repository.getAllRoutes(false) // TODO: Убрать заглушку
            .load()
            .doOnSuccess {
                routesSubject.onNext(it)
            }
            .emptySubscribe()
            .autoDispose()
    }

    fun openUserDetails() {
        router.navigateTo(Screens.userDetails())
    }
}