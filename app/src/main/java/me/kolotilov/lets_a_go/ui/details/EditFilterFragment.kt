package me.kolotilov.lets_a_go.ui.details

import android.animation.LayoutTransition
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.presentation.Constants
import me.kolotilov.lets_a_go.presentation.details.EditFilterViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import me.kolotilov.lets_a_go.ui.base.GroundFactory
import me.kolotilov.lets_a_go.ui.base.Recycler
import me.kolotilov.lets_a_go.ui.base.TypeFactory
import me.kolotilov.lets_a_go.ui.buildArguments
import me.kolotilov.lets_a_go.ui.distance
import me.kolotilov.lets_a_go.ui.dp
import me.kolotilov.lets_a_go.ui.duration
import org.joda.time.Duration
import org.kodein.di.instance

class EditFilterFragment @Deprecated(Constants.NEW_INSTANCE_MESSAGE) constructor() :
    BaseFragment(R.layout.fragment_edit_filter) {

    companion object {

        private const val TYPE = "TYPE"

        private const val DISTANCE_SCALE = 1000
        private const val MIN_DISTANCE = 0f
        private const val MAX_DISTANCE = 50f

        private const val DURATION_SCALE = 3_600_000
        private const val MIN_DURATION = 0f
        private const val MAX_DURATION = 10f

        @Suppress("DEPRECATION")
        fun newInstance(type: EditDetailsType): EditFilterFragment {
            return EditFilterFragment().buildArguments {
                putSerializable(TYPE, type)
            }
        }
    }

    override val viewModel: EditFilterViewModel by instance()

    override val toolbar: Toolbar by lazyView(R.id.toolbar)
    private val enabledSwitch: SwitchCompat by lazyView(R.id.enabled_switch)
    private val distanceTextView: TextView by lazyView(R.id.distance_text_view)
    private val distanceSlider: RangeSlider by lazyView(R.id.distance_slider)
    private val durationTextView: TextView by lazyView(R.id.duration_text_view)
    private val durationSlider: RangeSlider by lazyView(R.id.duration_slider)
    private val rootLayout: LinearLayout by lazyView(R.id.root_layout)
    private val typesRecycler: RecyclerView by lazyView(R.id.types_recycler)
    private val groundsRecycler: RecyclerView by lazyView(R.id.grounds_recycler)
    private val saveButton: Button by lazyView(R.id.save_button)
    private val nextButton: Button by lazyView(R.id.next_button)

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

    override fun Bundle.readArguments() {
        val type = getSerializable(BaseChooseFragment.TYPE) as EditDetailsType
        val onboarding = type == EditDetailsType.ONBOARDING
        nextButton.isVisible = onboarding
        saveButton.isVisible = !onboarding
    }

    override fun fillViews() {
        rootLayout.layoutTransition = LayoutTransition().apply {
            enableTransitionType(LayoutTransition.CHANGING)
        }

        typesRecycler.apply {
            adapter = typesAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
        }

        groundsRecycler.apply {
            adapter = groundsAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
        }

        distanceSlider.apply {
            valueFrom = MIN_DISTANCE
            valueTo = MAX_DISTANCE
            stepSize = 1f
            setLabelFormatter { (it * DISTANCE_SCALE).toDouble().distance(requireContext()) }
            addOnSliderTouchListener(getOnSliderTouchListener())
        }
        durationSlider.apply {
            valueFrom = MIN_DURATION
            valueTo = MAX_DURATION
            stepSize = 0.5f
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
        saveButton.setOnClickListener { viewModel.save() }
        enabledSwitch.setOnCheckedChangeListener { _, isChecked -> viewModel.setEnabled(isChecked) }
    }

    override fun subscribe() {
        viewModel.data.subscribe { data ->
            distanceSlider.values =
                data.distance.toList().map { (it / DISTANCE_SCALE).toInt().toFloat() }
            durationSlider.values =
                data.duration.toList().map { (it.millis / DURATION_SCALE).toInt().toFloat() }
            enabledSwitch.isChecked = data.enabled
            animateLayoutChanges = true
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

        viewModel.types.subscribe { types ->
            typesAdapter.items = types.first
            typesAdapter.selectedItems = types.second
        }.autoDispose()

        viewModel.grounds.subscribe { grounds ->
            groundsAdapter.items = grounds.first
            groundsAdapter.selectedItems = grounds.second
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