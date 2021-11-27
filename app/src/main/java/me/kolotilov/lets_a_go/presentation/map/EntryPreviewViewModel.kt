package me.kolotilov.lets_a_go.presentation.map

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import me.kolotilov.lets_a_go.models.EntryPreview
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.Results
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.presentation.base.BaseBottomSheetViewModel
import me.kolotilov.lets_a_go.ui.base.sendResult
import me.kolotilov.lets_a_go.utils.invoke
import org.joda.time.Duration
import ru.terrakok.cicerone.Router

class EntryPreviewViewModel(
    private val repository: Repository,
    private val router: Router
) : BaseBottomSheetViewModel() {

    data class Data(
        val distance: Double,
        val duration: Duration,
        val altitudeDelta: Double,
        val speed: Double,
        val kilocaloriesBurnt: Int?,
        val passed: Boolean,
    )

    val data: Observable<Data> get() = dataSubject
    private val dataSubject = BehaviorSubject.create<Data>()

    private lateinit var entryPreview: EntryPreview
    private lateinit var points: List<Point>
    private var result: EntryPreviewResult = EntryPreviewResult.LoadRoutes

    override fun attach() {
        dataSubject.onNext(
            Data(
                distance = entryPreview.distance,
                duration = entryPreview.duration,
                altitudeDelta = entryPreview.altitudeDelta,
                speed = entryPreview.speed,
                kilocaloriesBurnt = entryPreview.kiloCaloriesBurnt,
                passed = entryPreview.passed
            )
        )
    }

    override fun detach() {
        super.detach()
        router.sendResult(Results.ENTRY_PREVIEW, result)
    }

    fun init(entryPreview: EntryPreview, points: List<Point>) {
        this.entryPreview = entryPreview
        this.points = points
    }

    fun save() {
        result = EntryPreviewResult.DoNothing
        repository.createEntry(entryPreview.routeId, points)
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

 sealed class EntryPreviewResult {

    object LoadRoutes : EntryPreviewResult()
    object DoNothing : EntryPreviewResult()
}