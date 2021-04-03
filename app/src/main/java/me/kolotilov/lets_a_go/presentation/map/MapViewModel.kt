package me.kolotilov.lets_a_go.presentation.map

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Params
import me.kolotilov.lets_a_go.presentation.Screens
import ru.terrakok.cicerone.Router

class MapViewModel(
    private val repository: Repository,
    private val router: Router,
    private val params: Params
) : BaseViewModel() {

    val routes: Observable<List<Route>> get() = routesSubject
    private val routesSubject = BehaviorSubject.create<List<Route>>()

    override fun attach() {
        loadRoutes()
    }

    fun openEditRouteBottomSheet(recordedPoints: MutableList<Point>) {
        params.editRoute.points = recordedPoints
        router.navigateTo(Screens.EditRoute { loadRoutes() })
    }

    fun openRouteDetailsBottomSheet(route: Route) {
        params.routeDetails.id = route.id
        router.navigateTo(Screens.RouteDetails { loadRoutes() })
    }

    fun loadRoutes() {
        repository.getAllRoutes()
            .load()
            .doOnSuccess {
                routesSubject.onNext(it)
            }
            .emptySubscribe()
            .autoDispose()
    }
}