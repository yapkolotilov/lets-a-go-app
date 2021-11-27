package me.kolotilov.lets_a_go.ui.map

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import io.reactivex.Completable
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.EntryPreview
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.presentation.Constants
import me.kolotilov.lets_a_go.presentation.map.EntryPreviewViewModel
import me.kolotilov.lets_a_go.presentation.map.EntryStatsView
import me.kolotilov.lets_a_go.ui.*
import me.kolotilov.lets_a_go.ui.base.BaseBottomSheetFragment
import me.kolotilov.lets_a_go.utils.castTo
import org.kodein.di.instance
import java.util.concurrent.TimeUnit

class EntryPreviewBottomSheet @Deprecated(Constants.NEW_INSTANCE_MESSAGE) constructor() :
    BaseBottomSheetFragment(R.layout.fragment_edit_entry) {

    companion object {

        private const val PARAMS = "PARAMS"
        private const val POINTS = "POINTS"

        @Suppress("DEPRECATION")
        fun newInstance(entryPreview: EntryPreview, points: List<Point>): EntryPreviewBottomSheet {
            return EntryPreviewBottomSheet().buildArguments {
                putSerializable(PARAMS, entryPreview.toEntryPreviewParams())
                putSerializable(POINTS, points.map { it.toPointParam() }.toTypedArray())
            }
        }
    }

    override val viewModel: EntryPreviewViewModel by instance()
    override val peekHeight: Int get() = 72.dp(requireContext())

    private val passedTextView: TextView by lazyView(R.id.passed_text_view)
    private val statsView: EntryStatsView by lazyView(R.id.entry_stats_view)
    private val saveButton: Button by lazyView(R.id.save_button)
    private val deleteButton: Button by lazyView(R.id.delete_button)

    override fun Bundle.readArguments() {
        val preview = getSerializable(PARAMS)?.castTo<EntryPreviewParams>()?.toEntryPreview()!!
        val points = getSerializable(POINTS)?.castTo<Array<PointParam>>()?.map { it.toPoint() }!!
        viewModel.init(preview, points)
    }

    override fun bind() {
        deleteButton.setOnClickListener { dismiss() }
        saveButton.setOnClickListener { viewModel.save() }
    }

    override fun subscribe() {
        viewModel.data.subscribe {
            passedTextView.text =
                if (it.passed) getString(R.string.route_passed) else getString(R.string.route_not_passed)
            statsView.setData(
                distance = it.distance,
                duration = it.duration,
                speed = it.speed,
                kiloCaloriesBurnt = it.kilocaloriesBurnt,
                altitudeDelta = it.altitudeDelta,
                date = null,
            )
            Completable.complete()
                .delay(50, TimeUnit.MILLISECONDS)
                .schedule()
                .subscribe {
                    expand()
                }
                .autoDispose()
        }.autoDispose()
    }
}