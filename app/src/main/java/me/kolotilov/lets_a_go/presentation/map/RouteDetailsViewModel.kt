package me.kolotilov.lets_a_go.presentation.map

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.models.distance
import me.kolotilov.lets_a_go.models.duration
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Params
import me.kolotilov.lets_a_go.presentation.Screens
import org.joda.time.Duration
import ru.terrakok.cicerone.Router

class RouteDetailsViewModel(
    private val repository: Repository,
    private val params: Params,
    private val router: Router
) : BaseViewModel() {

    class Data(
        val name: String?,
        val type: Route.Type?,
        val ground: Route.Ground?,
        val distance: Double,
        val duration: Duration,
        val mine: Boolean
    )

    val data: Observable<Data> get() = dataSubject
    private val dataSubject = BehaviorSubject.create<Data>()

    override fun attach() {
        repository.getRoute(params.routeDetails.id ?: 0)
            .load()
            .doOnSuccess {
                parseRoute(it)
            }
            .emptySubscribe()
            .autoDispose()
    }

    override fun detach() {
        super.detach()
        params.routeDetails.clear()
    }

    fun go() {
        // TODO
    }

    fun edit() {
        params.editRoute.id = params.routeDetails.id
        router.navigateTo(Screens.EditRoute())
    }

    private fun parseRoute(route: Route) {
        dataSubject.onNext(
            Data(
                name = route.name,
                type = route.type,
                ground = route.ground,
                distance = route.points.distance(),
                duration = route.points.duration(),
                mine = true // TODO: Убрать заглушку на mine
            )
        )
    }
}