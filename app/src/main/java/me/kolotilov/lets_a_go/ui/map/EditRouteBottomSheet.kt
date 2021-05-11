package me.kolotilov.lets_a_go.ui.map

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputLayout
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.presentation.Constants
import me.kolotilov.lets_a_go.presentation.map.EditRouteViewModel
import me.kolotilov.lets_a_go.presentation.map.EntryStatsView
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
        private const val ID = "ID"

        @Suppress("DEPRECATION")
        fun newInstance(params: EditRouteParams?, id: Int?): EditRouteBottomSheet {
            return EditRouteBottomSheet().buildArguments {
                putSerializable(PREVIEW, params)
                putInt(ID, id ?: -1)
            }
        }
    }

    override val viewModel by instance<EditRouteViewModel>()
    override val peekHeight: Int get() = 72.dp(requireContext())

    private val titleTextView: TextView by lazyView(R.id.title_text_view)
    private val publicSwitch: SwitchCompat by lazyView(R.id.public_switch)
    private val nameEditText: TextInputLayout by lazyView(R.id.name_text_input)
    private val typeRecycler: RecyclerView by lazyView(R.id.type_recycler)
    private val groundRecycler: RecyclerView by lazyView(R.id.ground_recycler)
    private val difficultySlider: Slider by lazyView(R.id.difficulty_slider)
    private val statsView: EntryStatsView by lazyView(R.id.route_stats_view)
    private val saveButton: Button by lazyView(R.id.save_button)
    private val deleteButton: Button by lazyView(R.id.delete_button)
    private lateinit var typeAdapter: Recycler.SelectAdapter<Route.Type>
    private lateinit var groundAdapter: Recycler.SelectAdapter<Route.Ground>

    override fun Bundle.readArguments() {
        val preview = getSerializable(PREVIEW)?.castTo<EditRouteParams>()
        val id = getInt(ID).takeIf { it > 0 }
        viewModel.init(
            preview = preview?.toRoutePreview(),
            points = preview?.points?.map { it.toPoint() },
            id = id
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
                altitudeDelta = it.altitudeDelta,
                date = null
            )
            publicSwitch.isEnabled = it.public
            nameEditText.text = it.name ?: ""
            typeAdapter.selectedItem = it.type
            groundAdapter.selectedItem = it.ground
            if (it.difficulty != null)
                difficultySlider.value = it.difficulty.toFloat()
            expand()
        }.autoDispose()

        viewModel.isNew.subscribe {
            titleTextView.text =
                if (it) getString(R.string.new_route_title) else getString(R.string.route_title)
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