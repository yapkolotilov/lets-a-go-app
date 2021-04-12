package me.kolotilov.lets_a_go.presentation

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.models.Filter
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.models.RouteItem
import me.kolotilov.lets_a_go.network.Repository
import org.joda.time.Duration
import ru.terrakok.cicerone.Router
import java.util.concurrent.TimeUnit

class SearchRoutesViewModel(
    private val repository: Repository,
    private val router: Router
) : BaseViewModel() {

    val items: Observable<List<RouteItem>> get() = itemsSubject
    private val itemsSubject: Subject<List<RouteItem>> = BehaviorSubject.create()

    private val search: Observable<Unit>
        get() = searchSubject.debounce(
            500,
            TimeUnit.MILLISECONDS
        )
    private var searchSubject: Subject<Unit> = PublishSubject.create()

    val distance: Observable<Pair<Double, Double>> get() = distanceSubject
    private val distanceSubject: Subject<Pair<Double, Double>> = BehaviorSubject.create()

    val duration: Observable<Pair<Duration, Duration>> get() = durationSubject
    private val durationSubject: Subject<Pair<Duration, Duration>> = BehaviorSubject.create()

    val types: Observable<Set<Route.Type>> get() = typesSubject
    private val typesSubject: Subject<Set<Route.Type>> = BehaviorSubject.create()

    val grounds: Observable<Set<Route.Ground>> get() = groundsSubject
    private val groundsSubject: Subject<Set<Route.Ground>> = BehaviorSubject.create()

    private var distanceCache: ClosedFloatingPointRange<Double>? = null
    private var durationCache: ClosedRange<Duration>? = null
    private var selectedTypesCache: MutableSet<Route.Type> = mutableSetOf()
    private var selectedGroundsCache: MutableSet<Route.Ground> = mutableSetOf()
    private var enabledCache: Boolean = false
    private var queryCache: String = ""

    override fun attach() {
        fun search() {
            val filter = Filter(
                length = distanceCache,
                duration = durationCache,
                typesAllowed = selectedTypesCache.toList(),
                groundsAllowed = selectedGroundsCache.toList(),
                enabled = enabledCache
            )
            repository.searchRoutes(
                name = queryCache.takeIf { it.isNotEmpty() },
                filter = filter,
                location = repository.lastLocation
            )
                .load()
                .doOnSuccess {
                    itemsSubject.onNext(it)
                }
                .emptySubscribe()
                .autoDispose()
        }

        search
            .load()
            .doOnNext {
                search()
            }
            .emptySubscribe()
            .autoDispose()

        search()
    }

    fun search(query: String) {
        queryCache = query
        searchSubject.onNext(Unit)
    }

    fun search() {
        searchSubject.onNext(Unit)
    }

    fun setDistance(min: Float, max: Float) {
        val newDistance = min.toDouble()..max.toDouble()
        distanceCache = newDistance
        distanceSubject.onNext(min.toDouble() to max.toDouble())
    }

    fun setDuration(min: Duration, max: Duration) {
        durationCache = min..max
        durationSubject.onNext(min to max)
    }

    fun select(type: Route.Type) {
        if (selectedTypesCache.contains(type))
            selectedTypesCache.remove(type)
        else
            selectedTypesCache.add(type)
        typesSubject.onNext(selectedTypesCache)
    }

    fun select(ground: Route.Ground) {
        if (selectedGroundsCache.contains(ground))
            selectedGroundsCache.remove(ground)
        selectedGroundsCache.add(ground)
        groundsSubject.onNext(selectedGroundsCache)
    }

    fun setEnabled(enabled: Boolean) {
        enabledCache = enabled
    }

    fun openRoute(routeId: Int) {
        router.exit()
        router.replaceScreen(Screens.map(routeId = routeId, entryId = null))
    }
}