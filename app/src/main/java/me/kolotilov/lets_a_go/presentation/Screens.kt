package me.kolotilov.lets_a_go.presentation

import me.kolotilov.lets_a_go.ui.auth.LoginFragment
import me.kolotilov.lets_a_go.ui.auth.RegisterFragment
import me.kolotilov.lets_a_go.ui.details.BasicInfoFragment
import me.kolotilov.lets_a_go.ui.details.ChooseIllnessesFragment
import me.kolotilov.lets_a_go.ui.details.ChooseSymptomsFragment
import me.kolotilov.lets_a_go.ui.details.onboarding.OnboardingTitleFragment
import me.kolotilov.lets_a_go.ui.details.user.UserDetailsFragment
import me.kolotilov.lets_a_go.ui.map.*
import ru.terrakok.cicerone.android.support.SupportAppScreen

class
Screens {

    object LoginScreen : SupportAppScreen() {
        override fun getFragment() = LoginFragment()
    }

    object RegisterScreen : SupportAppScreen() {
        override fun getFragment() = RegisterFragment()
    }

    object BasicInfoScreen : SupportAppScreen() {
        override fun getFragment() = BasicInfoFragment()
    }

    object ChooseIllnessesScreen : SupportAppScreen() {
        override fun getFragment() = ChooseIllnessesFragment()
    }

    object ChooseSymptomsScreen : SupportAppScreen() {
        override fun getFragment() = ChooseSymptomsFragment()
    }

    object UserDetailsScreen : SupportAppScreen() {
        override fun getFragment() = UserDetailsFragment()
    }

    object OnboardingTitleScreen : SupportAppScreen() {
        override fun getFragment() = OnboardingTitleFragment()
    }

    object MapScreen : SupportAppScreen() {
        override fun getFragment() = MapFragment()
    }

    object EditRoute : SupportAppScreen() {
        override fun getFragment() = EditRouteBottomSheet()
    }

    object RouteDetails : SupportAppScreen() {
        override fun getFragment() = RouteDetailsBottomSheet()
    }

    object EditEntry : SupportAppScreen() {
        override fun getFragment() = EditEntryBottomSheet()
    }

    object EntryDetails : SupportAppScreen() {
        override fun getFragment() = EntryDetailsBottomSheet()
    }
}