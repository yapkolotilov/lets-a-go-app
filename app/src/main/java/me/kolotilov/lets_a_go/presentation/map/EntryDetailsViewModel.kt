package me.kolotilov.lets_a_go.presentation.map

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import me.kolotilov.lets_a_go.presentation.Params
import me.kolotilov.lets_a_go.presentation.base.BaseBottomSheetViewModel
import org.joda.time.DateTime
import org.joda.time.Duration

class EntryDetailsViewModel(
    private val params: Params
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
                finished = params.entryDetails.route?.let { params.entryDetails.entry.finished(it) } ?: false,
                date = params.entryDetails.entry.startDate(),
                duration = params.entryDetails.entry.duration(),
                distance = params.entryDetails.entry.distance(),
                calories = 100.0 // TODO
            )
        )
    }

    override fun detach() {
        super.detach()
        params.entryDetails.clear()
    }
}