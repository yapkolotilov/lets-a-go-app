package me.kolotilov.lets_a_go.ui.details.onboarding

import android.widget.Button
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.details.onboarding.OnboardingTitleViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import org.kodein.di.instance

class OnboardingTitleFragment : BaseFragment(R.layout.fragment_onboarding_title) {

    override val viewModel: OnboardingTitleViewModel by instance()

    private val fillButton: Button by lazyView(R.id.fill_button)
    private val skipButton: Button by lazyView(R.id.skip_button)

    override fun bind() {
        fillButton.setOnClickListener { viewModel.fill() }
        skipButton.setOnClickListener { viewModel.skip() }
    }
}