package me.kolotilov.lets_a_go.presentation.map

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.models.distance
import me.kolotilov.lets_a_go.models.duration
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.Params
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.presentation.base.BaseBottomSheetViewModel
import me.kolotilov.lets_a_go.ui.base.Grid
import org.joda.time.Duration
import ru.terrakok.cicerone.Router

class EditRouteViewModel(
    private val params: Params,
    private val repository: Repository,
    private val router: Router
) : BaseBottomSheetViewModel() {

    class Data(
        val duration: Duration,
        val distance: Double
    )

    val data: Observable<Data> get() = dataSubject
    private val dataSubject = BehaviorSubject.create<Data>()

    private var name: String? = null
    private var type: Route.Type? = null
    private var ground: Route.Ground? = null
    private var public: Boolean = false
    private var points = emptyList<Point>()
    private var id: Int? = null

    override fun attach() {
        points = params.editRoute.points
        id = params.editRoute.id
        if (points.isNotEmpty()) {
            dataSubject.onNext(
                Data(
                    points.duration(),
                    points.distance()
                )
            )
        } else if (id != null) {
            repository.getRoute(id!!)
                .load()
                .doOnSuccess {
                    dataSubject.onNext(
                        Data(
                            it.points.duration(),
                            it.points.distance()
                        )
                    )
                }
                .emptySubscribe()
                .autoDispose()
        }
    }

    override fun detach() {
        super.detach()
        params.editRoute.clear()
    }

    fun setName(name: String) {
        this.name = name
    }

    fun setType(type: Route.Type?) {
        this.type = type
    }

    fun setGround(ground: Route.Ground?) {
        this.ground = ground
    }

    fun setPublic(public: Boolean) {
        this.public = public
    }

    fun save() {
        fun parseResult(route: Route) {
            params.routeDetails.id = route.id
            router.exit()
            router.navigateTo(Screens.RouteDetails)
        }

        if (id == null) {
            repository.createRoute(name, type, ground, points)
                .load()
                .doOnSuccess {
                    parseResult(it)
                }
                .emptySubscribe()
                .autoDispose()
        } else {
            val route = Route(
                name = name,
                difficulty = null,
                type = type,
                ground = ground,
                points = points,
                entries = emptyList(),
                id = id ?: 0
            )
            repository.editRoute(id ?: 0, route)
                .load()
                .doOnSuccess {
                    parseResult(it)
                }
                .emptySubscribe()
                .autoDispose()
        }
    }

    fun delete() {
        if (id == null) {
            router.exit()
        } else
            repository.deleteRoute(id ?: 0)
                .load()
                .doOnComplete {
                    repeat(2) { router.exit() }
                }
                .emptySubscribe()
                .autoDispose()
    }

    override fun onDismiss() {
        params.editRoute.callback()
    }
}

class KeyValueModel(
    val key: String,
    val value: String
) : Grid.ViewModel