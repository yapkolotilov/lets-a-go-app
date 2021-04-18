package me.kolotilov.lets_a_go.presentation.details.onboarding

import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.ui.details.EditDetailsType
import ru.terrakok.cicerone.Router

class OnboardingTitleViewModel(
    private val router: Router
) : BaseViewModel() {

    fun fill() {
        router.newRootScreen(Screens.basicInfo(EditDetailsType.ONBOARDING))
    }

    fun skip() {
        router.navigateTo(Screens.map( ))
    }
}