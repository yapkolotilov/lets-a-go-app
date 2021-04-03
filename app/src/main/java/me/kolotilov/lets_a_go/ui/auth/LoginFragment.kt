package me.kolotilov.lets_a_go.ui.auth

import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.auth.LoginViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import org.kodein.di.instance

class LoginFragment : BaseFragment(R.layout.fragment_auth) {

    override val viewModel: LoginViewModel by instance()

    private val emailInput: EditText by lazyView(R.id.email_edit_text)
    private val passwordInput: EditText by lazyView(R.id.password_edit_text)
    private val loginButton: Button by lazyView(R.id.login_button)
    private val registerButton: Button by lazyView(R.id.register_button)

    override fun bind() {
        loginButton.setOnClickListener {
            viewModel.login(emailInput.text.toString(), passwordInput.text.toString())
        }
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
        viewModel.updateLoginButton(emailInput.text.toString(), passwordInput.text.toString())
    }
}