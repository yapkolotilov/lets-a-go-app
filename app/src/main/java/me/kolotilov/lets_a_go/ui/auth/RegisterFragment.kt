package me.kolotilov.lets_a_go.ui.auth

import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.auth.RegisterViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import org.kodein.di.instance

class RegisterFragment : BaseFragment(R.layout.fragment_register) {

    override val viewModel: RegisterViewModel by instance()

    private val emailInput: EditText by lazyView(R.id.email_edit_text)
    private val passwordInput: EditText by lazyView(R.id.password_edit_text)
    private val repeatPasswordInput: EditText by lazyView(R.id.repeat_password_edit_text)
    private val registerButton: Button by lazyView(R.id.register_button)

    override fun bind() {
        emailInput.doAfterTextChanged { updateRegisterButton() }
        passwordInput.doAfterTextChanged { updateRegisterButton() }
        repeatPasswordInput.doAfterTextChanged { updateRegisterButton() }
        registerButton.setOnClickListener { viewModel.register(emailInput.text.toString(), passwordInput.text.toString()) }
    }

    override fun subscribe() {
        viewModel.validation.subscribe {
            registerButton.isEnabled = it
        }.autoDispose()
    }

    private fun updateRegisterButton() {
        viewModel.updateRegisterButton(emailInput.text.toString(), passwordInput.text.toString(), repeatPasswordInput.text.toString())
    }
}