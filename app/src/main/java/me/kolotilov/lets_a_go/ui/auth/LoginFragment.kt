package me.kolotilov.lets_a_go.ui.auth

import android.view.View
import android.widget.Button
import com.google.android.material.textfield.TextInputLayout
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.ErrorCode
import me.kolotilov.lets_a_go.presentation.auth.LoginViewModel
import me.kolotilov.lets_a_go.presentation.base.ButtonData
import me.kolotilov.lets_a_go.presentation.base.showDialog
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import me.kolotilov.lets_a_go.ui.doAfterTextChanged
import me.kolotilov.lets_a_go.ui.text
import org.kodein.di.instance

/**
 * Экран логина в приложение.
 */
class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private class Data(
        val email: String,
        val password: String
    )

    override val viewModel: LoginViewModel by instance()

    private val emailInput: TextInputLayout by lazyView(R.id.email_text_input)
    private val passwordInput: TextInputLayout by lazyView(R.id.password_text_input)
    private val loginButton: View by lazyView(R.id.login_button)
    private val registerButton: Button by lazyView(R.id.register_button)

    override fun bind() {
        loginButton.setOnClickListener {
            val data = getData()
            viewModel.login(data.email, data.password)
        }
        registerButton.setOnClickListener { viewModel.register() }
        emailInput.doAfterTextChanged { updateLoginButton() }
        passwordInput.doAfterTextChanged { updateLoginButton() }
    }

    override fun subscribe() {
        viewModel.loginEnabled.subscribe {
            loginButton.isEnabled = it
        }.autoDispose()

        viewModel.errorDialog.subscribe {
            val okButton = ButtonData(getString(R.string.ok_button))
            when (it) {
                ErrorCode.USER_NOT_EXISTS -> showDialog(
                    title = getString(R.string.invalid_email_title),
                    message = getString(R.string.invalid_email_message),
                    positiveButton = okButton
                )
                ErrorCode.INVALID_PASSWORD -> showDialog(
                    title = getString(R.string.invalid_password_title),
                    message = getString(R.string.invalid_password_message),
                    positiveButton = okButton,
                )
                else -> Unit
            }
        }.autoDispose()
    }

    private fun updateLoginButton() {
        val data = getData()
        viewModel.updateLoginButton(data.email, data.password)
    }

    private fun getData(): Data {
        return Data(emailInput.text, passwordInput.text)
    }
}