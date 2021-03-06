package me.kolotilov.lets_a_go.presentation.map

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.models.RoutePreview
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Results
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.ui.base.sendResult
import me.kolotilov.lets_a_go.utils.invoke
import org.joda.time.Duration
import ru.terrakok.cicerone.Router

class EditRouteViewModel(
    private val repository: Repository,
    private val router: Router
) : BaseViewModel() {

    class Data(
        val name: String?,
        val public: Boolean,
        val distance: Double,
        val duration: Duration,
        val speed: Double,
        val kiloCaloriesBurnt: Int?,
        val altitudeDelta: Double,
        val type: Route.Type?,
        val ground: Route.Ground?,
        val difficulty: Int?
    )

    val isNew: Observable<Boolean> get() = isNewSubject
    private val isNewSubject: Subject<Boolean> = BehaviorSubject.create()

    val data: Observable<Data> get() = dataSubject
    private val dataSubject = BehaviorSubject.create<Data>()

    private var name: String? = null
    private var type: Route.Type? = null
    private var ground: Route.Ground? = null
    private var public: Boolean = false
    private var difficulty: Int = 0

    private var preview: RoutePreview? = null
    private var points: List<Point>? = null
    private var id: Int? = null
    private var result: EditRouteResult = EditRouteResult.Nothing

    override fun attach() {
        val preview = preview
        val id = id
        if (preview != null) {
            dataSubject.onNext(preview.toData())
            type = preview.type
            difficulty = preview.difficulty
        } else if (id != null) {
            repository.getRoute(id)
                .load()
                .doOnSuccess {
                    val data = Data(
                        name = it.name,
                        public = it.public,
                        distance = it.distance,
                        duration = it.duration,
                        speed = it.speed,
                        kiloCaloriesBurnt = it.kilocaloriesBurnt,
                        altitudeDelta = it.altitudeDelta,
                        type = it.type,
                        ground = it.ground,
                        difficulty = it.difficulty
                    )
                    dataSubject.onNext(data)
                }
                .emptySubscribe()
                .autoDispose()
        }
    }

    override fun detach() {
        super.detach()
        router.sendResult(Results.EDIT_ROUTE, result)
    }

    fun init(preview: RoutePreview?, points: List<Point>?, id: Int?) {
        this.preview = preview
        this.points = points
        this.id = id
        isNewSubject.onNext(id == null)
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
        if (id == null) {
            result = EditRouteResult.NewRoute(type)
            repository.createRoute(name, type, difficulty, ground, public,  points ?: emptyList())
                .load()
                .doOnSuccess {
                    router {
                        exit()
                        navigateTo(Screens.routeDetails(it.id))
                    }
                }
                .emptySubscribe()
                .autoDispose()
        } else {
            val id = id ?: 0
            result = EditRouteResult.Edited
            repository.editRoute(id, name, difficulty, type, ground)
                .load()
                .doOnSuccess {
                    router {
                        exit()
                        navigateTo(Screens.routeDetails(it.id))
                    }
                }
                .emptySubscribe()
                .autoDispose()
        }
    }

    fun delete() {
        result = EditRouteResult.Edited
        if (id == null) {
            router.exit()
        } else
            repository.deleteRoute(id ?: 0)
                .load()
                .doOnComplete {
                    router.exit()
                }
                .emptySubscribe()
                .autoDispose()
    }

    private fun RoutePreview.toData() = Data(
        name = name,
        public = public,
        distance = distance,
        duration = duration,
        speed = speed,
        kiloCaloriesBurnt = kiloCaloriesBurnt,
        altitudeDelta = altitudeDelta,
        type = type,
        ground = ground,
        difficulty = difficulty
    )
}

sealed class EditRouteResult {

    data class NewRoute(
        val type: Route.Type?
    ) : EditRouteResult()

    object Edited : EditRouteResult()

    object Nothing : EditRouteResult()
}