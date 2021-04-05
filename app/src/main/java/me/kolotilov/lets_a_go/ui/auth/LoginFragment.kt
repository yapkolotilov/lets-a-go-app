package me.kolotilov.lets_a_go.ui.auth

import android.widget.Button
import com.google.android.material.textfield.TextInputLayout
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.auth.LoginViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import me.kolotilov.lets_a_go.ui.doAfterTextChanged
import me.kolotilov.lets_a_go.ui.text
import org.kodein.di.instance

class LoginFragment : BaseFragment(R.layout.fragment_auth) {

    override val viewModel: LoginViewModel by instance()

    private val emailInput: TextInputLayout by lazyView(R.id.email_text_input)
    private val passwordInput: TextInputLayout by lazyView(R.id.password_text_input)
    private val loginButton: Button by lazyView(R.id.login_button)
    private val registerButton: Button by lazyView(R.id.register_button)

    override fun bind() {
        loginButton.setOnClickListener { viewModel.login(emailInput.text, passwordInput.text) }
        registerButton.setOnClickListener { viewModel.register() }
        emailInput.doAfterTextChanged { updateLoginButton() }
        passwordInput.doAfterTextChanged { updateLoginButton() }
    }

    override fun subscribe() {
        viewModel.loginEnabled.subscribe {
            loginButton.isEnabled = it
        }.autoDispose()
    }

    private fun updateLoginButton() {
        viewModel.updateLoginButton(emailInput.text, passwordInput.text)
    }
}