package me.kolotilov.lets_a_go.ui.map

import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Entry
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.presentation.map.KeyValueModel
import me.kolotilov.lets_a_go.presentation.map.RouteDetailsViewModel
import me.kolotilov.lets_a_go.ui.base.BaseBottomSheetFragment
import me.kolotilov.lets_a_go.ui.base.Grid
import me.kolotilov.lets_a_go.ui.base.Recycler
import org.kodein.di.instance
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class RouteDetailsBottomSheet : BaseBottomSheetFragment(R.layout.fragment_route_details) {

    override val viewModel by instance<RouteDetailsViewModel>()

    private val editImageView by lazyView<ImageView>(R.id.edit_image_view)
    private val nameTextView by lazyView<TextView>(R.id.name_text_view)
    private val typeRecycler by lazyView<RecyclerView>(R.id.type_recycler)
    private val groundRecycler by lazyView<RecyclerView>(R.id.ground_recycler)
    private val statsGrid by lazyView<GridLayout>(R.id.stats_grid)
    private val entriesTitle by lazyView<TextView>(R.id.entries_title_text_view)
    private val entriesRecycler by lazyView<RecyclerView>(R.id.entries_recycler)
    private val goButton by lazyView<Button>(R.id.go_button)
    private lateinit var typeAdapter: Recycler.SelectAdapter<Route.Type>
    private lateinit var groundAdapter: Recycler.SelectAdapter<Route.Ground>
    private lateinit var statsAdapter: Grid.ListAdapter
    private lateinit var entriesAdapter: Recycler.Adapter<Pair<Entry, Route>>

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

        entriesAdapter = Recycler.Adapter(EntryFactory(), object : Recycler.Delegate<Pair<Entry, Route>> {

            override fun onClick(item: Pair<Entry, Route>) {
                viewModel.openEntryDetails(item)
            }
        })
        entriesRecycler.adapter = entriesAdapter
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
            entriesTitle.isVisible= data.entries.isNotEmpty()
            entriesRecycler.isVisible = data.entries.isNotEmpty()
            entriesAdapter.items = data.entries
        }.autoDispose()
    }
}

class EntryFactory : Recycler.Factory<Pair<Entry, Route>> {

    override fun getType(item: Pair<Entry, Route>): Int {
        return R.layout.entry_detail_item
    }

    override fun getViewHolder(
        type: Int,
        view: View,
        delegate: Recycler.Delegate<Pair<Entry, Route>>
    ): Recycler.ViewHolder<Pair<Entry, Route>> {
        return EntryDetailsViewHolder(view, delegate)
    }
}

class EntryDetailsViewHolder(itemView: View, delegate: Recycler.Delegate<Pair<Entry, Route>>) :
    Recycler.ViewHolder<Pair<Entry, Route>>(itemView, delegate) {

    private val dateTextView = itemView.findViewById<TextView>(R.id.date_text_view)
    private val distanceTextView = itemView.findViewById<TextView>(R.id.distance_text_view)
    private val decimalFormat = DecimalFormat().apply { maximumFractionDigits = 1 }
    private lateinit var currentItem: Pair<Entry, Route>

    init {
        itemView.setOnClickListener {
            delegate.onClick(currentItem)
        }
    }

    override fun bind(item: Pair<Entry, Route>, selected: Boolean) {
        currentItem = item
        dateTextView.text = SimpleDateFormat("dd MMMMM HH:mm:ss").format(item.first.points.first().timestamp.toDate())
        distanceTextView.text = "${decimalFormat.format(item.first.distance() / 1000)} км."
        itemView.setBackgroundColor(if (item.first.finished(item.second)) Color.GREEN else Color.RED)
    }
}