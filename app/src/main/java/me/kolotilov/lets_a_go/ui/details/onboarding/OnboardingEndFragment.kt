package me.kolotilov.lets_a_go.ui.details.onboarding

import android.widget.Button
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.details.onboarding.OnboardingEndViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import org.kodein.di.instance

class OnboardingEndFragment : BaseFragment(R.layout.fragment_onboarding_end) {

    override val viewModel: OnboardingEndViewModel by instance()

    private val goButton: Button by lazyView(R.id.go_button)

    override fun bind() {
        goButton.setOnClickListener {
            viewModel.go()
        }
    }
}