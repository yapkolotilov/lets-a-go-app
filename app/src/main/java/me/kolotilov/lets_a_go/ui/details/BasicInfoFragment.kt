package me.kolotilov.lets_a_go.ui.details

import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.details.BasicInfoViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import me.kolotilov.lets_a_go.utils.smartSetText
import org.kodein.di.instance

class BasicInfoFragment : BaseFragment(R.layout.fragment_basic_info) {

    override val viewModel: BasicInfoViewModel by instance()

    private val nameInput: EditText by lazyView(R.id.name_edit_text)
    private val ageInput: EditText by lazyView(R.id.age_edit_text)
    private val heightInput: EditText by lazyView(R.id.height_edit_text)
    private val weightInput: EditText by lazyView(R.id.weight_edit_text)
    private val nextButton: Button by lazyView(R.id.next_button)
    private val skipButton: Button by lazyView(R.id.skip_button)

    override fun bind() {
        nameInput.doAfterTextChanged {
            val name = it.toString()
            viewModel.setName(name)
        }
        ageInput.doAfterTextChanged {
            val age = it.toString().toIntOrNull() ?: 0
            viewModel.setAge(age)
        }
        heightInput.doAfterTextChanged {
            val height = it.toString().toIntOrNull() ?: 0
            viewModel.setHeight(height)
        }
        weightInput.doAfterTextChanged {
            val weight = it.toString().toIntOrNull() ?: 0
            viewModel.setWeight(weight)
        }
        nextButton.setOnClickListener { viewModel.next() }
        skipButton.setOnClickListener { viewModel.skip() }
    }

    override fun subscribe() {
        viewModel.name.subscribe {
            nameInput.smartSetText(it)
        }.autoDispose()

        viewModel.age.subscribe {
            ageInput.smartSetText(it)
        }.autoDispose()

        viewModel.height.subscribe {
            heightInput.smartSetText(it)
        }.autoDispose()

        viewModel.weight.subscribe {
            weightInput.smartSetText(it)
        }.autoDispose()

        viewModel.validation.subscribe {
            nextButton.isEnabled = it
        }.autoDispose()
    }
}