package me.kolotilov.lets_a_go.presentation.details

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.models.Filter
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import org.joda.time.Duration
import ru.terrakok.cicerone.Router

class EditFilterViewModel(
    private val repository: Repository,
    private val router: Router
) : BaseViewModel() {

    data class Data(
        val distance: Pair<Double, Double>,
        val duration: Pair<Duration, Duration>,
        val types: List<Route.Type>,
        val grounds: List<Route.Ground>,
        val enabled: Boolean
    )

    val data: Observable<Data> get() = dataSubject
    private val dataSubject: Subject<Data> = BehaviorSubject.create()

    val distance: Observable<Pair<Double, Double>> get() = distanceSubject
    private val distanceSubject: Subject<Pair<Double, Double>> = BehaviorSubject.create()

    val duration: Observable<Pair<Duration, Duration>> get() = durationSubject
    private val durationSubject: Subject<Pair<Duration, Duration>> = BehaviorSubject.create()

    val types: Observable<Pair<List<Route.Type>, Set<Route.Type>>> get() = typesSubject
    private val typesSubject: Subject<Pair<List<Route.Type>, Set<Route.Type>>> =
        BehaviorSubject.create()

    val grounds: Observable<Pair<List<Route.Ground>, Set<Route.Ground>>> get() = groundsSubject
    private val groundsSubject: Subject<Pair<List<Route.Ground>, Set<Route.Ground>>> =
        BehaviorSubject.create()

    private var distanceCache: ClosedFloatingPointRange<Double>? = null
    private var durationCache: ClosedRange<Duration>? = null
    private var typesCache: MutableList<Route.Type> = mutableListOf()
    private var groundsCache: MutableList<Route.Ground> = mutableListOf()
    private var selectedTypesCache: MutableSet<Route.Type> = mutableSetOf()
    private var selectedGroundsCache: MutableSet<Route.Ground> = mutableSetOf()
    private var enabledCache: Boolean = false
    private var edited: Boolean = false

    override fun attach() {
        repository.getDetails()
            .map { it.filter }
            .load()
            .doOnSuccess {
                parseFilter(it)
            }
            .emptySubscribe()
            .autoDispose()
    }

    fun setDistance(min: Float, max: Float) {
        edited = true
        val newDistance = min.toDouble()..max.toDouble()
        distanceCache = newDistance
        distanceSubject.onNext(min.toDouble() to max.toDouble())
    }

    fun setDuration(min: Duration, max: Duration) {
        edited = true
        durationCache = min..max
        durationSubject.onNext(min to max)
    }

    fun select(type: Route.Type) {
        edited = true
        if (selectedTypesCache.contains(type))
            selectedTypesCache.remove(type)
        else
            selectedTypesCache.add(type)
        typesSubject.onNext(typesCache to selectedTypesCache)
    }

    fun select(ground: Route.Ground) {
        edited = true
        if (selectedGroundsCache.contains(ground))
            selectedGroundsCache.remove(ground)
        selectedGroundsCache.add(ground)
        groundsSubject.onNext(groundsCache to selectedGroundsCache)
    }

    fun setEnabled(enabled: Boolean) {

    }

    fun save() {
        repository.editDetails(
            filter = Filter(
                length = distanceCache,
                duration = durationCache,
                typesAllowed = selectedTypesCache.toList(),
                groundsAllowed = selectedGroundsCache.toList(),
                enabled = enabledCache,
            )
        ).load()
            .doOnSuccess {
                router.exit()
            }
            .emptySubscribe()
            .autoDispose()
    }

    private fun parseFilter(filter: Filter) {
        val data = Data(
            distance = filter.length?.let { Pair(it.start, it.endInclusive) } ?: Pair(
                0.0,
                50_000.0
            ),
            duration = filter.duration?.let { Pair(it.start, it.endInclusive) } ?: Pair(
                Duration(0),
                Duration.standardHours(10)
            ),
            types = filter.typesAllowed ?: Route.Type.values().toList(),
            grounds = filter.groundsAllowed ?: Route.Ground.values().toList(),
            enabled = filter.enabled
        )
        enabledCache = data.enabled
        typesCache = Route.Type.values().toMutableList()
        selectedTypesCache = filter.typesAllowed?.toMutableSet() ?: mutableSetOf()
        typesSubject.onNext(typesCache to selectedTypesCache)

        groundsCache = Route.Ground.values().toMutableList()
        selectedGroundsCache = filter.groundsAllowed?.toMutableSet() ?: mutableSetOf()
        groundsSubject.onNext(groundsCache to selectedGroundsCache)

        durationSubject.onNext(data.duration)
        distanceSubject.onNext(data.distance)
        dataSubject.onNext(data)
    }
}