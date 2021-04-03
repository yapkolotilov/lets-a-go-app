package me.kolotilov.lets_a_go.presentation.details.onboarding

import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Screens
import ru.terrakok.cicerone.Router

class OnboardingTitleViewModel(
    private val router: Router
) : BaseViewModel() {

    fun fill() {
        router.navigateTo(Screens.BasicInfoScreen)
    }

    fun skip() {
        router.navigateTo(Screens.UserDetailsScreen)
    }
}