package me.kolotilov.lets_a_go.ui.map

import android.widget.GridLayout
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.map.EntryDetailsViewModel
import me.kolotilov.lets_a_go.ui.base.BaseBottomSheetFragment
import me.kolotilov.lets_a_go.ui.base.Grid
import me.kolotilov.lets_a_go.ui.base.KeyValueFactory
import me.kolotilov.lets_a_go.ui.base.KeyValueModel
import org.kodein.di.instance
import java.text.SimpleDateFormat

class EntryDetailsBottomSheet : BaseBottomSheetFragment(R.layout.fragment_entry_details) {

    override val viewModel by instance<EntryDetailsViewModel>()
    private val statsGrid by lazyView<GridLayout>(R.id.stats_grid)
    private lateinit var statsAdapter: Grid.ListAdapter

    override fun fillViews() {
        statsAdapter = Grid.ListAdapter(statsGrid, KeyValueFactory())
    }

    override fun subscribe() {
        viewModel.data.subscribe {
            statsAdapter.items = listOf(
                KeyValueModel("Пройден", if (it.finished) "Да" else "Нет"),
                KeyValueModel("Дата", SimpleDateFormat("dd MMMMM HH:mm:ss").format(it.date.millis)),
                KeyValueModel("Время", SimpleDateFormat("HH:mm:ss").format(it.duration.millis)),
                KeyValueModel("Расстояние", "${it.distance / 1000} км."),
                KeyValueModel("Калории", "${it.calories} ккл")
            )
        }.autoDispose()
    }
}