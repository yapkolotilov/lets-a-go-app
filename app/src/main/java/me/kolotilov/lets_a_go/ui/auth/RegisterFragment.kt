package me.kolotilov.lets_a_go.ui.auth

import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputLayout
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.ErrorCode
import me.kolotilov.lets_a_go.presentation.auth.RegisterViewModel
import me.kolotilov.lets_a_go.presentation.base.ButtonData
import me.kolotilov.lets_a_go.presentation.base.showDialog
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import me.kolotilov.lets_a_go.ui.doAfterTextChanged
import me.kolotilov.lets_a_go.ui.text
import org.kodein.di.instance

/**
 * Экран регистрации.
 */
class RegisterFragment : BaseFragment(R.layout.fragment_register) {

    private data class Data(
        val email: String,
        val password: String,
        val repeatPassword: String
    )

    override val viewModel: RegisterViewModel by instance()

    override val toolbar: Toolbar by lazyView(R.id.toolbar)
    private val emailInput: TextInputLayout by lazyView(R.id.email_edit_text)
    private val passwordInput: TextInputLayout by lazyView(R.id.password_edit_text)
    private val repeatPasswordInput: TextInputLayout by lazyView(R.id.repeat_password_edit_text)
    private val registerButton: Button by lazyView(R.id.register_button)

    override fun bind() {
        emailInput.doAfterTextChanged { updateRegisterButton() }
        passwordInput.doAfterTextChanged { updateRegisterButton() }
        repeatPasswordInput.doAfterTextChanged { updateRegisterButton() }
        registerButton.setOnClickListener {
            val data = getData()
            viewModel.register(data.email, data.password)
        }
    }

    override fun subscribe() {
        viewModel.validation.subscribe {
            registerButton.isEnabled = it
        }.autoDispose()

        viewModel.error.subscribe {
            val okButton = ButtonData(getString(R.string.ok_button))
            when (it) {
                ErrorCode.INVALID_PASSWORD -> showDialog(
                    title = getString(R.string.min_password_characters_error_title),
                    positiveButton = okButton
                )
                ErrorCode.USER_ALREADY_EXITS -> showDialog(
                    title = getString(R.string.user_already_registered_title),
                    message = getString(R.string.user_already_registered_message),
                    positiveButton = okButton
                )
                else -> Unit
            }
        }.autoDispose()
    }

    private fun updateRegisterButton() {
        val data = getData()
        viewModel.updateRegisterButton(
            email = data.email,
            password = data.password,
            repeatPassword = data.repeatPassword
        )
    }

    private fun getData(): Data {
        return Data(
            email = emailInput.text,
            password = passwordInput.text,
            repeatPassword = repeatPasswordInput.text
        )
    }
}