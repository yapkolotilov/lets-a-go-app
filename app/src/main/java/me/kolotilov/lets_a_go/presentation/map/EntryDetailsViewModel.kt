package me.kolotilov.lets_a_go.presentation.map

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.base.BaseBottomSheetViewModel
import org.joda.time.DateTime
import org.joda.time.Duration

class EntryDetailsViewModel(
    private val repository: Repository
) : BaseBottomSheetViewModel() {

    data class Data(
        val finished: Boolean,
        val date: DateTime,
        val duration: Duration,
        val distance: Double,
        val speed: Double,
        val altitudeDelta: Double,
        val kiloCaloriesBurnt: Int?,
        val routeId: Int?
    )

    val data: Observable<Data> get() = dataSubject
    private val dataSubject = BehaviorSubject.create<Data>()

    private var id: Int = 0

    override fun attach() {
        repository.getEntry(id)
            .load()
            .doOnSuccess {
                dataSubject.onNext(
                    Data(
                        finished = it.finished,
                        date = it.date,
                        duration = it.duration,
                        distance = it.distance,
                        speed = it.speed,
                        altitudeDelta = it.altitudeDelta,
                        kiloCaloriesBurnt = it.kiloCaloriesBurnt,
                        routeId = it.routeId
                    )
                )
            }
            .emptySubscribe()
            .autoDispose()
    }

    fun init(id: Int) {
        this.id = id
    }
}