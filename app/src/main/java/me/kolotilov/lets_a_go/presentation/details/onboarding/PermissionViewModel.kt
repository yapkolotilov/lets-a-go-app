package me.kolotilov.lets_a_go.presentation.details.onboarding

import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Screens
import ru.terrakok.cicerone.Router

class PermissionViewModel(
    private val router: Router
) : BaseViewModel() {

    fun go() {
        router.navigateTo(Screens.onboardingEnd())
    }
}