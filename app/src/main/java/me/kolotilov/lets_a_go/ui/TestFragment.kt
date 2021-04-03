package me.kolotilov.lets_a_go.ui

import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.EmptyViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import org.kodein.di.instance

class TestFragment : BaseFragment(R.layout.fragment_test) {

    override val viewModel: EmptyViewModel by instance()
}