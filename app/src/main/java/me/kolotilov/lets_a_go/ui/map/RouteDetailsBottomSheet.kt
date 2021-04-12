package me.kolotilov.lets_a_go.ui.map

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.presentation.Constants
import me.kolotilov.lets_a_go.presentation.map.EntryStatsView
import me.kolotilov.lets_a_go.presentation.map.RouteDetailsViewModel
import me.kolotilov.lets_a_go.ui.base.*
import me.kolotilov.lets_a_go.ui.buildArguments
import me.kolotilov.lets_a_go.ui.dp
import org.kodein.di.instance

class RouteDetailsBottomSheet @Deprecated(Constants.NEW_INSTANCE_MESSAGE) constructor() :
    BaseBottomSheetFragment(R.layout.fragment_route_details) {

    companion object {

        private const val ID = "ID"

        @Suppress("DEPRECATION")
        fun newInstance(id: Int): RouteDetailsBottomSheet {
            return RouteDetailsBottomSheet().buildArguments {
                putInt(ID, id)
            }
        }
    }

    override val viewModel by instance<RouteDetailsViewModel>()
    override val peekHeight: Int get() = 72.dp(requireContext())

    private val editImageView: ImageView by lazyView(R.id.edit_image_view)
    private val titleTextView: TextView by lazyView(R.id.title_text_view)
    private val typesHintTextView: TextView by lazyView(R.id.types_hint_text_view)
    private val typeRecycler: RecyclerView by lazyView(R.id.type_recycler)
    private val groundHintTextView: TextView by lazyView(R.id.ground_hint_text_view)
    private val groundRecycler by lazyView<RecyclerView>(R.id.ground_recycler)
    private val statsView: EntryStatsView by lazyView(R.id.route_stats_view)
    private val entriesTitle: TextView by lazyView(R.id.entries_title_text_view)
    private val entriesView: EntriesView by lazyView(R.id.entries_view)
    private val difficultyHint: TextView by lazyView(R.id.difficulty_hint_text_view)
    private val difficultySlider: Slider by lazyView(R.id.difficulty_slider)
    private val goButton: Button by lazyView(R.id.go_button)

    private lateinit var typeAdapter: Recycler.SelectAdapter<Route.Type>
    private lateinit var groundAdapter: Recycler.SelectAdapter<Route.Ground>

    override fun Bundle.readArguments() {
        viewModel.init(getInt(ID))
    }

    override fun fillViews() {
        typeAdapter = Recycler.SelectAdapter(TypeFactory())
        typeAdapter.enabled = false
        groundAdapter = Recycler.SelectAdapter(
            Recycler.SimpleFactory(R.layout.ground_type) { view, delegate ->
                GroundViewHolder(view, delegate)
            },
        )
        groundAdapter.enabled = false

        typeRecycler.adapter = typeAdapter
        typeRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        groundRecycler.adapter = groundAdapter
        groundRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        entriesView.setOnEntryClickListener {
            viewModel.openEntryDetails(it)
        }
    }

    override fun bind() {
        editImageView.setOnClickListener { viewModel.edit() }
        goButton.setOnClickListener { viewModel.go() }
    }

    override fun subscribe() {
        viewModel.data.subscribe { data ->
            editImageView.isVisible = data.mine
            titleTextView.text = data.name?.takeIf { it.isNotEmpty() } ?: getString(R.string.route_title)

            if (data.type != null) {
                typeAdapter.items = listOf(data.type)
                typeAdapter.selectedItems = setOf(data.type)
            } else {
                typesHintTextView.isVisible = false
                typeRecycler.isVisible = false
            }

            if (data.ground != null) {
                groundAdapter.items = listOf(data.ground)
                groundAdapter.selectedItems = setOf(data.ground)
            } else {
                groundHintTextView.isVisible = false
                groundRecycler.isVisible = false
            }

            if (data.difficulty == null)
                difficultySlider.isVisible = false
            else
                difficultySlider.value = data.difficulty.toFloat()

            statsView.setData(
                distance = data.distance,
                duration = data.duration,
                speed = data.speed,
                kiloCaloriesBurnt = data.kilocaloriesBurnt,
                altitudeDelta = data.altitudeDelta,
                date = null,
            )

            if (data.entries.isEmpty()) {
                entriesTitle.isVisible = false
                entriesView.isVisible = false
            } else {
                entriesView.setItems(
                    totalDistance = data.totalDistance,
                    totalKilocaloriesBurnt = data.totalCaloriesBurnt,
                    entries = data.entries
                )
            }
            expand()
        }.autoDispose()
    }
}