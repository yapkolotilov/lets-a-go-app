package me.kolotilov.lets_a_go.ui.details

import me.kolotilov.lets_a_go.presentation.details.ChooseIllnessesViewModel
import org.kodein.di.instance

class ChooseIllnessesFragment : BaseChooseFragment() {

    override val viewModel: ChooseIllnessesViewModel by instance()
}