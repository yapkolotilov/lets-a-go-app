package me.kolotilov.lets_a_go.ui.map

import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.presentation.map.KeyValueModel
import me.kolotilov.lets_a_go.presentation.map.RouteDetailsViewModel
import me.kolotilov.lets_a_go.ui.base.BaseBottomSheetFragment
import me.kolotilov.lets_a_go.ui.base.Grid
import me.kolotilov.lets_a_go.ui.base.Recycler
import org.kodein.di.instance
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class RouteDetailsBottomSheet @Deprecated("newInstance()") constructor() : BaseBottomSheetFragment(R.layout.fragment_route_details) {

    companion object {

        @Suppress("DEPRECATION")
        fun newInstance(onDismiss: () -> Unit): RouteDetailsBottomSheet {
            return RouteDetailsBottomSheet().also { it.onDismiss = onDismiss }
        }
    }

    override val viewModel by instance<RouteDetailsViewModel>()

    private val editImageView by lazyView<ImageView>(R.id.edit_image_view)
    private val nameTextView by lazyView<TextView>(R.id.name_text_view)
    private val typeRecycler by lazyView<RecyclerView>(R.id.type_recycler)
    private val groundRecycler by lazyView<RecyclerView>(R.id.ground_recycler)
    private val statsGrid by lazyView<GridLayout>(R.id.stats_grid)
    private val goButton by lazyView<Button>(R.id.go_button)
    private lateinit var typeAdapter: Recycler.SelectAdapter<Route.Type>
    private lateinit var groundAdapter: Recycler.SelectAdapter<Route.Ground>
    private lateinit var statsAdapter: Grid.ListAdapter

    override fun fillViews() {
        typeAdapter = Recycler.SelectAdapter(
            Recycler.SimpleFactory(
                type = R.layout.route_type,
            ) { view, delegate ->
                TypeViewHolder(view, delegate)
            },
            enabled = false
        )
        groundAdapter = Recycler.SelectAdapter(
            Recycler.SimpleFactory(R.layout.ground_type) { view, delegate ->
                GroundViewHolder(view, delegate)
            },
            enabled = false
        )
        statsAdapter = Grid.ListAdapter(statsGrid, StatsFactory())

        typeRecycler.adapter = typeAdapter
        typeRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        groundRecycler.adapter = groundAdapter
        groundRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    override fun bind() {
        editImageView.setOnClickListener { viewModel.edit() }
        goButton.setOnClickListener { viewModel.go() }
    }

    override fun subscribe() {
        val dateFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        viewModel.data.subscribe { data ->
            editImageView.isVisible = data.mine
            nameTextView.text = data.name
            if (data.type != null) {
                typeAdapter.items = listOf(data.type)
                typeAdapter.selectedItems = setOf(data.type)
            }
            if (data.ground != null) {
                groundAdapter.items = listOf(data.ground)
                groundAdapter.selectedItems = setOf(data.ground)
            }
            statsAdapter.items = listOf(
                KeyValueModel("Расстояние:", data.distance.roundToInt().toString() + " м."),
                KeyValueModel("Продолжительность:", dateFormatter.format(data.duration.millis))
            )
        }.autoDispose()
    }
}