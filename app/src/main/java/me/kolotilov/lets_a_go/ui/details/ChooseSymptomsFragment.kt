package me.kolotilov.lets_a_go.ui.details

import me.kolotilov.lets_a_go.presentation.details.ChooseSymptomsViewModel
import org.kodein.di.instance

class ChooseSymptomsFragment : BaseChooseFragment() {

    override val viewModel: ChooseSymptomsViewModel by instance()
}