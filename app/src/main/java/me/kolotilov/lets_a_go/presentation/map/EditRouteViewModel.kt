package me.kolotilov.lets_a_go.presentation.map

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.models.RoutePreview
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.ui.base.sendResult
import org.joda.time.Duration
import ru.terrakok.cicerone.Router

class EditRouteViewModel(
    private val repository: Repository,
    private val router: Router
) : BaseViewModel() {

    companion object {

        const val TAG = "EditRoute"
    }

    class Data(
        val distance: Double,
        val duration: Duration,
        val speed: Double,
        val kiloCaloriesBurnt: Int?,
        val altitudeDelta: Double,
        val type: Route.Type,
        val difficulty: Int
    )

    val data: Observable<Data> get() = dataSubject
    private val dataSubject = BehaviorSubject.create<Data>()

    private var name: String? = null
    private var type: Route.Type? = null
    private var ground: Route.Ground? = null
    private var public: Boolean = false
    private var difficulty: Int? = null

    private var preview: RoutePreview? = null
    private var points: List<Point>? = null
    private var id: Int? = null

    override fun attach() {
        val preview = preview
        if (preview != null) {
            dataSubject.onNext(preview.toData())
        } else if (id != null) {
            repository.getRoute(id!!)
                .load()
                .doOnSuccess {
                    // TODO
                }
                .emptySubscribe()
                .autoDispose()
        }
    }

    override fun detach() {
        super.detach()
        router.sendResult(TAG, Unit)
    }

    fun init(preview: RoutePreview?, points: List<Point>?) {
        this.preview = preview
        this.points = points
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

    fun setDifficulty(difficulty: Int) {
        this.difficulty = difficulty
    }

    fun save() {
        fun parseResult(route: Route) {
//            params.routeDetails.id = route.id
            router.exit()
            router.navigateTo(Screens.routeDetails())
        }

        // TODO
        if (id == null) {
            repository.createRoute(name, type, ground, points ?: emptyList())
                .load()
                .doOnSuccess {
                    parseResult(it)
                }
                .emptySubscribe()
                .autoDispose()
        } else {
//            val route = Route(
//                name = name,
//                difficulty = null,
//                type = type,
//                ground = ground,
//                points = points,
//                entries = emptyList(),
//                id = id ?: 0
//            )
//            repository.editRoute(id ?: 0, route)
//                .load()
//                .doOnSuccess {
//                    parseResult(it)
//                }
//                .emptySubscribe()
//                .autoDispose()
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

    private fun RoutePreview.toData() = Data(
        distance = distance,
        duration = duration,
        speed = speed,
        kiloCaloriesBurnt = kiloCaloriesBurnt,
        altitudeDelta = altitudeDelta,
        type = type,
        difficulty = difficulty
    )
}