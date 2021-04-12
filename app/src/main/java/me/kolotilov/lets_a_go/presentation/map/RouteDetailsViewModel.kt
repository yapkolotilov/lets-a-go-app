package me.kolotilov.lets_a_go.presentation.map

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.models.RouteDetails
import me.kolotilov.lets_a_go.models.RouteEntry
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Results
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.ui.base.sendResult
import org.joda.time.Duration
import ru.terrakok.cicerone.Router

class RouteDetailsViewModel(
    private val repository: Repository,
    private val router: Router
) : BaseViewModel() {

    class Data(
        val name: String?,
        val distance: Double,
        val duration: Duration,
        val altitudeDelta: Double,
        val speed: Double,
        val kilocaloriesBurnt: Int?,
        val difficulty: Int?,
        val type: Route.Type?,
        val ground: Route.Ground?,
        val entries: List<RouteEntry>,
        val mine: Boolean,
        val totalDistance: Double,
        val totalCaloriesBurnt: Int?,
        val id: Int,
    )

    val data: Observable<Data> get() = dataSubject
    private val dataSubject = BehaviorSubject.create<Data>()

    private var result: Boolean = false
    private var id: Int = 0

    override fun attach() {
        repository.getRoute(id)
            .load()
            .doOnSuccess {
                parseRoute(it)
            }
            .emptySubscribe()
            .autoDispose()
    }

    override fun detach() {
        super.detach()
        router.sendResult(Results.LOAD_ROUTES, result)
    }

    fun init(id: Int) {
        this.id = id
    }

    fun go() {
        result = true
        router.exit()
    }

    fun edit() {
        router.replaceScreen(Screens.editRoute(id = id))
    }

    private fun parseRoute(route: RouteDetails) {
        dataSubject.onNext(
            Data(
                name = route.name,
                distance = route.distance,
                duration = route.duration,
                altitudeDelta = route.altitudeDelta,
                speed = route.speed,
                difficulty = route.difficulty,
                entries = route.entries,
                ground = route.ground,
                kilocaloriesBurnt = route.kilocaloriesBurnt,
                type = route.type,
                mine = route.mine,
                totalDistance = route.totalDistance,
                totalCaloriesBurnt = route.totalCaloriesBurnt,
                id = route.id,
            )
        )
    }

    fun openEntryDetails(item: RouteEntry) {
        router.navigateTo(Screens.entryDetails(item.id))
    }
}