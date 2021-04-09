package me.kolotilov.lets_a_go.presentation

import androidx.annotation.AnimRes
import androidx.fragment.app.Fragment
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.ui.EditRouteParams
import me.kolotilov.lets_a_go.ui.auth.LoginFragment
import me.kolotilov.lets_a_go.ui.auth.RegisterFragment
import me.kolotilov.lets_a_go.ui.details.*
import me.kolotilov.lets_a_go.ui.details.onboarding.OnboardingEndFragment
import me.kolotilov.lets_a_go.ui.details.onboarding.OnboardingTitleFragment
import me.kolotilov.lets_a_go.ui.details.user.UserDetailsFragment
import me.kolotilov.lets_a_go.ui.map.*
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

    fun login() = LetsScreen { LoginFragment() }

    fun register() = LetsScreen(animation = ScreenAnimation.SLIDE_LEFT) { RegisterFragment() }

    /**
     * Экран редактирования базовой информации.
     *
     */
    fun basicInfo(type: EditDetailsType) = LetsScreen { EditBasicInfoFragment.newInstance(type) }

    fun chooseIllnesses(type: EditDetailsType) =
        LetsScreen { ChooseIllnessesFragment.newInstance(type) }

    fun chooseSymptoms(type: EditDetailsType) =
        LetsScreen { ChooseSymptomsFragment.newInstance(type) }

    fun userDetails() = LetsScreen { UserDetailsFragment() }

    fun onboarding() = LetsScreen { OnboardingTitleFragment() }

    fun map() = LetsScreen { MapFragment() }

    fun editRoute(params: EditRouteParams) = LetsScreen { EditRouteBottomSheet.newInstance(params) }

    fun routeDetails() = LetsScreen { RouteDetailsBottomSheet() }

    fun editEntry() = LetsScreen { EditEntryBottomSheet() }

    fun entryDetails() = LetsScreen { EntryDetailsBottomSheet() }

    fun editFilter(type: EditDetailsType) = LetsScreen { EditFilterFragment.newInstance(type) }

    fun onboardingEnd() = LetsScreen { OnboardingEndFragment() }
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