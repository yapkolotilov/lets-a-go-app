package me.kolotilov.lets_a_go.presentation.map

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.Params
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.presentation.base.BaseBottomSheetViewModel
import org.joda.time.DateTime
import org.joda.time.Duration
import ru.terrakok.cicerone.Router

class EditEntryViewModel(
    private val repository: Repository,
    private val params: Params,
    private val router: Router
) : BaseBottomSheetViewModel() {

    data class Data(
        val finished: Boolean,
        val date: DateTime,
        val duration: Duration,
        val distance: Double,
        val calories: Double
    )

    val data: Observable<Data> get() = dataSubject
    private val dataSubject = BehaviorSubject.create<Data>()

    override fun attach() {
        dataSubject.onNext(
            Data(
                finished = params.editEntry.route?.let { params.editEntry.entry.finished(it) } ?: false,
                date = params.editEntry.entry.startDate(),
                duration = params.editEntry.entry.duration(),
                distance = params.editEntry.entry.distance(),
                calories = 100.0 // TODO
            )
        )
    }

    fun save() {
        repository.createEntry(params.editEntry.route ?: return, params.editEntry.entry)
            .load()
            .doOnSuccess {
                params.routeDetails.id = it.id
                router.exit()
                params.routeDetails.id = params.editEntry.route?.id
                router.navigateTo(Screens.routeDetails())
            }
            .emptySubscribe()
            .autoDispose()
    }

    override fun detach() {
        super.detach()
        params.editEntry.clear()
    }

    override fun onDismiss() {
        params.editEntry.callback()
    }
}