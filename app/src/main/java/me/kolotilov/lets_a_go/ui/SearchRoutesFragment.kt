package me.kolotilov.lets_a_go.ui

import android.animation.LayoutTransition
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.models.RouteItem
import me.kolotilov.lets_a_go.presentation.SearchRoutesViewModel
import me.kolotilov.lets_a_go.ui.base.*
import org.joda.time.Duration
import org.kodein.di.instance

class SearchRoutesFragment : BaseFragment(R.layout.fragment_search_routes) {

    companion object {

        private const val DISTANCE_SCALE = 1000
        private const val MIN_DISTANCE = 0f
        private const val MAX_DISTANCE = 50f

        private const val DURATION_SCALE = 3_600_000
        private const val MIN_DURATION = 0f
        private const val MAX_DURATION = 10f
    }

    override val viewModel: SearchRoutesViewModel by instance()
    override val toolbar: Toolbar by lazyView(R.id.toolbar)

    private val searchView: SearchView by lazyView(R.id.search_view)
    private val editFilterButton: View by lazyView(R.id.edit_filter_button)
    private val editFilterLayout: ViewGroup by lazyView(R.id.edit_filter_layout)
    private val enabledSwitch: SwitchCompat by lazyView(R.id.enabled_switch)
    private val distanceTextView: TextView by lazyView(R.id.distance_text_view)
    private val distanceSlider: RangeSlider by lazyView(R.id.distance_slider)
    private val durationTextView: TextView by lazyView(R.id.duration_text_view)
    private val durationSlider: RangeSlider by lazyView(R.id.duration_slider)
    private val typesRecycler: RecyclerView by lazyView(R.id.types_recycler)
    private val searchButton: Button by lazyView(R.id.search_filter_button)
    private val groundsRecycler: RecyclerView by lazyView(R.id.grounds_recycler)
    private val itemsRecycler: RecyclerView by lazyView(R.id.recycler)

    private val typesAdapter: Recycler.Adapter<Route.Type> =
        Recycler.Adapter(TypeFactory(), object : Recycler.Delegate<Route.Type> {

            override fun onClick(item: Route.Type) {
                viewModel.select(item)
            }
        })

    private val groundsAdapter: Recycler.Adapter<Route.Ground> =
        Recycler.Adapter(GroundFactory(), object : Recycler.Delegate<Route.Ground> {

            override fun onClick(item: Route.Ground) {
                viewModel.select(item)
            }
        })

    private val itemsAdapter: Recycler.Adapter<RouteItem> =
        Recycler.Adapter(RouteFactory(), object : Recycler.Delegate<RouteItem> {

            override fun onClick(item: RouteItem) {
                viewModel.openRoute(item.id)
            }
        })

    override fun fillViews() {
        animateLayoutChanges = true
        editFilterLayout.layoutTransition = LayoutTransition().apply {
            enableTransitionType(LayoutTransition.CHANGING)
        }
        editFilterButton.setOnClickListener {
            editFilterLayout.isVisible = !editFilterLayout.isVisible
        }
        typesRecycler.apply {
            adapter = typesAdapter
            typesAdapter.items = Route.Type.values().toList()
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
        }
        groundsRecycler.apply {
            adapter = groundsAdapter
            groundsAdapter.items = Route.Ground.values().toList()
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
        }
        itemsRecycler.adapter = itemsAdapter
        distanceSlider.apply {
            valueFrom = MIN_DISTANCE
            valueTo = MAX_DISTANCE
            stepSize = 1f
            values = listOf(valueFrom, valueTo)
            setLabelFormatter { (it * DISTANCE_SCALE).toDouble().distance(requireContext()) }
            addOnSliderTouchListener(getOnSliderTouchListener())
        }
        durationSlider.apply {
            valueFrom = MIN_DURATION
            valueTo = MAX_DURATION
            stepSize = 0.5f
            values = listOf(valueFrom, valueTo)
            setLabelFormatter { Duration((it * DURATION_SCALE).toLong()).duration() }
            addOnSliderTouchListener(getOnSliderTouchListener())
        }
    }

    override fun bind() {
        distanceSlider.addOnChangeListener { _, _, fromUser ->
            if (!fromUser)
                return@addOnChangeListener
            viewModel.setDistance(
                distanceSlider.values[0] * DISTANCE_SCALE,
                distanceSlider.values[1] * DISTANCE_SCALE
            )
        }
        durationSlider.addOnChangeListener { _, _, fromUser ->
            if (!fromUser)
                return@addOnChangeListener
            viewModel.setDuration(
                Duration((durationSlider.values[0] * DURATION_SCALE).toLong()),
                Duration((durationSlider.values[1] * DURATION_SCALE).toLong())
            )
        }
        enabledSwitch.setOnCheckedChangeListener { _, isChecked -> viewModel.setEnabled(isChecked) }

        searchView.doAfterTextChanged { viewModel.search(it) }
        searchButton.setOnClickListener {
            editFilterLayout.isVisible = false
            viewModel.search()
        }
    }

    override fun subscribe() {
        viewModel.types.subscribe { types ->
            typesAdapter.selectedItems = types
        }.autoDispose()

        viewModel.grounds.subscribe { grounds ->
            groundsAdapter.selectedItems = grounds
        }.autoDispose()

        viewModel.distance.subscribe { distance ->
            distanceTextView.text = getString(
                R.string.from_to,
                distance.first.distance(requireContext()),
                distance.second.distance(requireContext())
            )
        }.autoDispose()

        viewModel.duration.subscribe { duration ->
            durationTextView.text = getString(
                R.string.from_to,
                duration.first.duration(),
                duration.second.duration()
            )
        }.autoDispose()

        viewModel.items.subscribe {
            itemsAdapter.items = it
        }.autoDispose()
    }

    private fun getOnSliderTouchListener(): RangeSlider.OnSliderTouchListener {
        return object : RangeSlider.OnSliderTouchListener {

            override fun onStartTrackingTouch(slider: RangeSlider) {
                slider.layoutParams = LinearLayout.LayoutParams(slider.layoutParams).apply {
                    topMargin = 32.dp(requireContext())
                }
            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                slider.layoutParams = LinearLayout.LayoutParams(slider.layoutParams).apply {
                    topMargin = 0
                }
            }
        }
    }
}