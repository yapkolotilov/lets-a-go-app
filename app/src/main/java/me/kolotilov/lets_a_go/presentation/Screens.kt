package me.kolotilov.lets_a_go.presentation

import androidx.annotation.AnimRes
import androidx.fragment.app.Fragment
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.ui.auth.LoginFragment
import me.kolotilov.lets_a_go.ui.auth.RegisterFragment
import me.kolotilov.lets_a_go.ui.details.BasicInfoFragment
import me.kolotilov.lets_a_go.ui.details.ChooseIllnessesFragment
import me.kolotilov.lets_a_go.ui.details.ChooseSymptomsFragment
import me.kolotilov.lets_a_go.ui.details.onboarding.OnboardingTitleFragment
import me.kolotilov.lets_a_go.ui.details.user.UserDetailsFragment
import me.kolotilov.lets_a_go.ui.map.*
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

    fun login() = LetsScreen(animation = null) { LoginFragment() }

    fun register() = LetsScreen(animation = ScreenAnimation.SLIDE_LEFT) { RegisterFragment() }

    fun basicInfo() = LetsScreen { BasicInfoFragment() }

    fun chooseIllnesses() = LetsScreen { ChooseIllnessesFragment() }

    fun chooseSymptoms() = LetsScreen { ChooseSymptomsFragment() }

    fun userDetails() = LetsScreen { UserDetailsFragment() }

    fun onboarding() = LetsScreen { OnboardingTitleFragment() }

    fun map() = LetsScreen { MapFragment() }

    fun editRoute() = LetsScreen { EditRouteBottomSheet() }

    fun routeDetails() = LetsScreen { RouteDetailsBottomSheet() }

    fun editEntry() = LetsScreen { EditEntryBottomSheet() }

    fun entryDetails() = LetsScreen { EntryDetailsBottomSheet() }
}

class LetsScreen(
    val animation: ScreenAnimation? = ScreenAnimation.SLIDE_RIGHT,
    private val factory: () -> Fragment
) : SupportAppScreen() {

    override fun getFragment() = factory()
}

/**
 * Анимация.
 */
enum class ScreenAnimation(
    @AnimRes
    val enter: Int,
    @AnimRes
    val exit: Int,
    @AnimRes
    val popEnter: Int,
    @AnimRes
    val popExit: Int
) {

    SLIDE_LEFT(
        enter = R.anim.enter_from_left,
        exit = R.anim.exit_to_right,
        popEnter = R.anim.enter_from_right,
        popExit = R.anim.exit_to_left
    ),

    SLIDE_RIGHT(
        enter = R.anim.enter_from_right,
        exit = R.anim.exit_to_left,
        popEnter = R.anim.enter_from_left,
        popExit = R.anim.exit_to_right
    ),
}