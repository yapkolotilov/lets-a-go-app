package me.kolotilov.lets_a_go.ui.map

import android.os.Bundle
import android.widget.TextView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.Constants
import me.kolotilov.lets_a_go.presentation.map.EntryDetailsViewModel
import me.kolotilov.lets_a_go.presentation.map.EntryStatsView
import me.kolotilov.lets_a_go.ui.base.BaseBottomSheetFragment
import me.kolotilov.lets_a_go.ui.buildArguments
import me.kolotilov.lets_a_go.ui.dp
import org.kodein.di.instance

class EntryDetailsBottomSheet @Deprecated(Constants.NEW_INSTANCE_MESSAGE) constructor() :
    BaseBottomSheetFragment(R.layout.fragment_entry_details) {

    companion object {

        private const val ID = "ID"

        @Suppress("DEPRECATION")
        fun newInstance(id: Int): EntryDetailsBottomSheet {
            return EntryDetailsBottomSheet().buildArguments {
                putInt(ID, id)
            }
        }
    }

    override val viewModel: EntryDetailsViewModel by instance()
    private val finishedTextView: TextView by lazyView(R.id.finished_text_view)
    private val statsView: EntryStatsView by lazyView(R.id.entry_stats_view)
    override val peekHeight: Int get() = 64.dp(requireContext())

    override fun Bundle.readArguments() {
        val id = getInt(ID)
        viewModel.init(id)
    }

    override fun subscribe() {
        viewModel.data.subscribe {
            statsView.setData(
                distance = it.distance,
                duration = it.duration,
                speed = it.speed,
                kiloCaloriesBurnt = it.kiloCaloriesBurnt,
                altitudeDelta = it.altitudeDelta,
                date = it.date
            )
            finishedTextView.text =
                if (it.finished) getString(R.string.route_passed) else getString(R.string.route_not_passed)
            expand()
        }.autoDispose()
    }
}