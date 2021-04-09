package me.kolotilov.lets_a_go.ui.map

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputLayout
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.presentation.Constants
import me.kolotilov.lets_a_go.presentation.map.EditRouteViewModel
import me.kolotilov.lets_a_go.presentation.map.RouteStatsView
import me.kolotilov.lets_a_go.ui.*
import me.kolotilov.lets_a_go.ui.base.BaseBottomSheetFragment
import me.kolotilov.lets_a_go.ui.base.GroundFactory
import me.kolotilov.lets_a_go.ui.base.Recycler
import me.kolotilov.lets_a_go.ui.base.TypeFactory
import me.kolotilov.lets_a_go.utils.castTo
import org.kodein.di.instance
import java.util.*

class EditRouteBottomSheet @Deprecated(Constants.NEW_INSTANCE_MESSAGE) constructor() :
    BaseBottomSheetFragment(R.layout.fragment_edit_route) {

    companion object {

        private const val PREVIEW = "PREVIEW"

        @Suppress("DEPRECATION")
        fun newInstance(params: EditRouteParams): EditRouteBottomSheet {
            return EditRouteBottomSheet().buildArguments {
                putSerializable(PREVIEW, params)
            }
        }
    }

    override val viewModel by instance<EditRouteViewModel>()

    private val publicSwitch: SwitchCompat by lazyView(R.id.public_switch)
    private val nameEditText: TextInputLayout by lazyView(R.id.name_text_input)
    private val typeRecycler: RecyclerView by lazyView(R.id.type_recycler)
    private val groundRecycler: RecyclerView by lazyView(R.id.ground_recycler)
    private val difficultySlider: Slider by lazyView(R.id.difficulty_slider)
    private val statsView: RouteStatsView by lazyView(R.id.route_stats_view)
    private val saveButton: Button by lazyView(R.id.save_button)
    private val deleteButton: Button by lazyView(R.id.delete_button)
    private lateinit var typeAdapter: Recycler.SelectAdapter<Route.Type>
    private lateinit var groundAdapter: Recycler.SelectAdapter<Route.Ground>

    override fun Bundle.readArguments() {
        val preview = getSerializable(PREVIEW)?.castTo<EditRouteParams>()
        viewModel.init(
            preview = preview?.toRoutePreview(),
            points = preview?.points?.map { it.toPoint() }
        )
    }

    override fun fillViews() {
        animateLayoutChanges = true
        typeAdapter = Recycler.SelectAdapter(TypeFactory())
        groundAdapter = Recycler.SelectAdapter(GroundFactory())

        typeRecycler.adapter = typeAdapter
        typeRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        typeAdapter.items = Route.Type.values().toList()

        groundRecycler.adapter = groundAdapter
        groundRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        groundAdapter.items = Route.Ground.values().toList()

        difficultySlider.addOnSliderTouchListener(getOnSliderTouchListener())
    }

    override fun bind() {
        publicSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPublic(isChecked)
        }
        nameEditText.doAfterTextChanged {
            viewModel.setName(it)
        }
        typeAdapter.setOnItemSelectedListener {
            viewModel.setType(it)
        }
        groundAdapter.setOnItemSelectedListener {
            viewModel.setGround(it)
        }
        difficultySlider.addOnChangeListener { _, value, fromUser ->
            if (!fromUser)
                return@addOnChangeListener
            viewModel.setDifficulty(value.toInt())
        }
        saveButton.setOnClickListener {
            viewModel.save()
        }
        deleteButton.setOnClickListener {
            viewModel.delete()
        }
    }

    override fun subscribe() {
        viewModel.data.subscribe {
            statsView.setData(
                distance = it.distance,
                duration = it.duration,
                speed = it.speed,
                kiloCaloriesBurnt = it.kiloCaloriesBurnt,
                altitudeDelta = it.altitudeDelta
            )
            typeAdapter.selectedItem = it.type
            it.difficulty
            difficultySlider.value = it.difficulty.toFloat()
        }.autoDispose()
    }

    private fun getOnSliderTouchListener(): Slider.OnSliderTouchListener {
        return object : Slider.OnSliderTouchListener {

            override fun onStartTrackingTouch(slider: Slider) {
                slider.layoutParams = LinearLayout.LayoutParams(slider.layoutParams).apply {
                    topMargin = 32.dp(requireContext())
                }
            }

            override fun onStopTrackingTouch(slider: Slider) {
                slider.layoutParams = LinearLayout.LayoutParams(slider.layoutParams).apply {
                    topMargin = 0
                }
            }
        }
    }
}