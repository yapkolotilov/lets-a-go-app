package me.kolotilov.lets_a_go.ui.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.presentation.map.EditRouteViewModel
import me.kolotilov.lets_a_go.ui.base.*
import me.kolotilov.lets_a_go.ui.context
import org.kodein.di.instance
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class EditRouteBottomSheet : BaseBottomSheetFragment(R.layout.fragment_edit_route) {

    override val viewModel by instance<EditRouteViewModel>()

    private val publicSwitch by lazyView<SwitchCompat>(R.id.public_switch)
    private val nameEditText by lazyView<EditText>(R.id.name_edit_text)
    private val typeRecycler by lazyView<RecyclerView>(R.id.type_recycler)
    private val groundRecycler by lazyView<RecyclerView>(R.id.ground_recycler)
    private val statsGrid by lazyView<GridLayout>(R.id.stats_grid)
    private val saveButton by lazyView<Button>(R.id.save_button)
    private val deleteButton by lazyView<Button>(R.id.delete_button)
    private lateinit var typeAdapter: Recycler.SelectAdapter<Route.Type>
    private lateinit var groundAdapter: Recycler.SelectAdapter<Route.Ground>
    private lateinit var statsAdapter: Grid.ListAdapter

    override fun fillViews() {
        typeAdapter = Recycler.SelectAdapter(
            Recycler.SimpleFactory(
                type = R.layout.route_type
            ) { view, delegate ->
                TypeViewHolder(view, delegate)
            }
        )
        groundAdapter = Recycler.SelectAdapter(
            Recycler.SimpleFactory(R.layout.ground_type) { view, delegate ->
                GroundViewHolder(view, delegate)
            }
        )
        statsAdapter = Grid.ListAdapter(statsGrid, StatsFactory())

        typeRecycler.adapter = typeAdapter
        typeRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        typeAdapter.items = Route.Type.values().toList()

        groundRecycler.adapter = groundAdapter
        groundRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        groundAdapter.items = Route.Ground.values().toList()
    }

    override fun bind() {
        publicSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPublic(isChecked)
        }
        nameEditText.addTextChangedListener {
            viewModel.setName(it.toString())
        }
        typeAdapter.setOnItemSelectedListener {
            viewModel.setType(it)
        }
        groundAdapter.setOnItemSelectedListener {
            viewModel.setGround(it)
        }
        saveButton.setOnClickListener {
            viewModel.save()
        }
        deleteButton.setOnClickListener {
            viewModel.delete()
        }
    }

    override fun subscribe() {
        val dateFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        viewModel.data.subscribe {
            statsAdapter.items = listOf(
                KeyValueModel("Расстояние:", it.distance.roundToInt().toString() + " м."),
                KeyValueModel("Продолжительность:", dateFormatter.format(it.duration.millis))
            )
        }.autoDispose()

//        viewModel.dismiss.subscribe {
//            dismiss()
//        }.autoDispose()
    }
}


class TypeViewHolder(itemView: View, delegate: Recycler.Delegate<Route.Type>) :
    Recycler.ViewHolder<Route.Type>(itemView, delegate) {

    private val iconView: ImageView = itemView as ImageView

    override fun bind(item: Route.Type, selected: Boolean) {
        iconView.setBackgroundResource(if (selected) R.drawable.bg_selected_item else R.drawable.bg_select_item)
        val icon = when (item) {
            Route.Type.WALKING -> R.drawable.ic_type_walking
            Route.Type.RUNNING -> R.drawable.ic_type_running
            Route.Type.CYCLING -> R.drawable.ic_type_cycling
        }
        iconView.setImageResource(icon)
    }
}

class GroundViewHolder(itemView: View, delegate: Recycler.Delegate<Route.Ground>) :
    Recycler.ViewHolder<Route.Ground>(itemView, delegate) {

    private val iconView: TextView = itemView as TextView

    override fun bind(item: Route.Ground, selected: Boolean) {
        iconView.setBackgroundResource(if (selected) R.drawable.bg_selected_item else R.drawable.bg_select_item)
        val text = when (item) {
            Route.Ground.ASPHALT -> context.getString(R.string.ground_asphalt)
            Route.Ground.TRACK -> context.getString(R.string.ground_track)
        }
        iconView.text = text
    }
}

class StatsFactory : Grid.Factory {

    override fun holder(
        item: Grid.ViewModel,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): Grid.ViewHolder<*> {
        val keyView = TextView(parent.context)
        val valueView = TextView(parent.context)
        return KeyValueViewHolder(keyView, valueView)
    }
}