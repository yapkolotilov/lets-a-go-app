package me.kolotilov.lets_a_go.ui.auth

import android.os.Bundle
import android.widget.TextView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.Constants
import me.kolotilov.lets_a_go.presentation.auth.EmailViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import me.kolotilov.lets_a_go.ui.buildArguments
import org.kodein.di.instance

class EmailFragment @Deprecated(Constants.NEW_INSTANCE_MESSAGE) constructor() : BaseFragment(R.layout.fragment_email) {

    companion object {

        private const val EMAIL = "EMAIL"
        private const val PASSWORD = "PASSWORD"

        @Suppress("DEPRECATION")
        fun newInstance(email: String, password: String): EmailFragment {
            return EmailFragment().buildArguments {
                putString(EMAIL, email)
                putString(PASSWORD, password)
            }
        }
    }

    override val viewModel: EmailViewModel by instance()

    private val titleText: TextView by lazyView(R.id.title_text_view)

    override fun Bundle.readArguments() {
        val email = getString(EMAIL, "")
        val password = getString(PASSWORD, "")
        titleText.text = getString(R.string.confirm_email_text, email)
        viewModel.init(email, password)
    }
}