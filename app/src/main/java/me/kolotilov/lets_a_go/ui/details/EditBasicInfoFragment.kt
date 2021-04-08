package me.kolotilov.lets_a_go.ui.details

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.Tags
import me.kolotilov.lets_a_go.presentation.base.ButtonData
import me.kolotilov.lets_a_go.presentation.base.showCompat
import me.kolotilov.lets_a_go.presentation.base.showDialog
import me.kolotilov.lets_a_go.presentation.details.EditBasicInfoViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import me.kolotilov.lets_a_go.ui.base.NumberPickerDialog
import me.kolotilov.lets_a_go.ui.buildArguments
import me.kolotilov.lets_a_go.ui.doAfterTextChanged
import me.kolotilov.lets_a_go.ui.setTouchListener
import me.kolotilov.lets_a_go.ui.text
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.kodein.di.instance

class EditBasicInfoFragment : BaseFragment(R.layout.fragment_basic_info) {

    companion object {

        private const val TYPE = "TYPE"

        @Suppress("DEPRECATION")
        fun newInstance(type: EditDetailsType): EditBasicInfoFragment {
            return EditBasicInfoFragment().buildArguments {
                putInt(TYPE, type.ordinal)
            }
        }
    }

    override val viewModel: EditBasicInfoViewModel by instance()
    private val birthDateFormatter: DateTimeFormatter by instance(Tags.BIRTH_DATE)

    override val toolbar: Toolbar by lazyView(R.id.toolbar)
    private val nameInput: TextInputLayout by lazyView(R.id.name_text_input)
    private val birthDateInput: TextInputLayout by lazyView(R.id.birth_date_text_input)
    private val heightInput: TextInputLayout by lazyView(R.id.height_text_input)
    private val weightInput: TextInputLayout by lazyView(R.id.weight_text_input)
    private val saveButton: Button by lazyView(R.id.save_button)
    private val nextButton: Button by lazyView(R.id.next_button)

    override fun Bundle.readArguments() {
        val typeArg = getInt(BaseChooseFragment.TYPE, -1)
        val type = EditDetailsType.values().first { it.ordinal == typeArg }
        val onboarding = type == EditDetailsType.ONBOARDING
        nextButton.isVisible = onboarding
        saveButton.isVisible = !onboarding
    }

    override fun bind() {
        nameInput.doAfterTextChanged { viewModel.setName(it) }
        birthDateInput.setTouchListener { viewModel.openBirthDatePicker() }
        heightInput.setTouchListener { viewModel.openHeightPicker() }
        weightInput.setTouchListener { viewModel.openWeightPicker() }
        saveButton.setOnClickListener { viewModel.save() }
        nextButton.setOnClickListener { viewModel.next() }
    }

    override fun subscribe() {
        viewModel.data.subscribe { data ->
            nameInput.text = data.name ?: ""
            if (data.birthDate != null)
                birthDateInput.text = birthDateFormatter.print(data.birthDate)
            if (data.height != null)
                heightInput.text = getString(R.string.cm_value, data.height)
            if (data.weight != null)
                weightInput.text = getString(R.string.weight_value, data.weight)
        }.autoDispose()

        viewModel.datePicker.subscribe { date ->
            DatePickerDialog(requireContext(), { _, year, month, day ->
                val newDate = DateTime(year, month + 1, day, 0, 0)
                viewModel.setBirthDate(newDate)
            }, date.year, date.monthOfYear, date.dayOfMonth).showCompat()
        }.autoDispose()

        viewModel.heightPicker.subscribe { height ->
            NumberPickerDialog(
                requireContext(),
                title = getString(R.string.height_picker_title),
                valueRange = 50..272,
                initialValue = height
            ) {
                viewModel.setHeight(it)
            }.show()
        }.autoDispose()

        viewModel.weightPicker.subscribe { weight ->
            NumberPickerDialog(
                requireContext(),
                title = getString(R.string.weight_picker_title),
                valueRange = 2..635,
                initialValue = weight
            ) {
                viewModel.setWeight(it)
            }.show()
        }.autoDispose()

        viewModel.updateFilterDialog.subscribe {
            showDialog(
                title = getString(R.string.update_filter_title),
                message = getString(R.string.update_filter_message),
                positiveButton = ButtonData(getString(R.string.yes_button)) {
                    viewModel.performSave(true)
                },
                negativeButton = ButtonData(getString(R.string.no_button)) {
                    viewModel.performSave(false)
                }
            )
        }.autoDispose()
    }
}