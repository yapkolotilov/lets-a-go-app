package me.kolotilov.lets_a_go.di

import me.kolotilov.lets_a_go.App
import me.kolotilov.lets_a_go.presentation.EmptyViewModel
import me.kolotilov.lets_a_go.presentation.auth.LoginViewModel
import me.kolotilov.lets_a_go.presentation.auth.RegisterViewModel
import me.kolotilov.lets_a_go.presentation.details.BasicInfoViewModel
import me.kolotilov.lets_a_go.presentation.details.ChooseIllnessesViewModel
import me.kolotilov.lets_a_go.presentation.details.ChooseSymptomsViewModel
import me.kolotilov.lets_a_go.presentation.details.UserDetailsViewModel
import me.kolotilov.lets_a_go.presentation.details.onboarding.OnboardingTitleViewModel
import me.kolotilov.lets_a_go.presentation.map.EditRouteViewModel
import me.kolotilov.lets_a_go.presentation.map.MapViewModel
import me.kolotilov.lets_a_go.presentation.map.RouteDetailsViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import ru.terrakok.cicerone.NavigatorHolder

fun uiModule() = DI.Module("App") {
    bind<NavigatorHolder>() with provider { instance<App>().getNavigatorHolder() }
    bind<EmptyViewModel>() with provider { EmptyViewModel() }
    bind<LoginViewModel>() with provider { LoginViewModel(instance(), instance()) }
    bind<BasicInfoViewModel>() with provider { BasicInfoViewModel(instance(), instance()) }
    bind<UserDetailsViewModel>() with provider { UserDetailsViewModel(instance(), instance()) }
    bind<RegisterViewModel>() with provider { RegisterViewModel(instance(), instance()) }
    bind<ChooseIllnessesViewModel>() with provider {
        ChooseIllnessesViewModel(instance(), instance(), instance())
    }
    bind<ChooseSymptomsViewModel>() with provider {
        ChooseSymptomsViewModel(instance(), instance(), instance())
    }
    bind<OnboardingTitleViewModel>() with provider { OnboardingTitleViewModel(instance()) }
    bind<MapViewModel>() with provider { MapViewModel(instance(), instance(), instance()) }
    bind<EditRouteViewModel>() with provider { EditRouteViewModel(instance(), instance(), instance()) }
    bind<RouteDetailsViewModel>() with provider { RouteDetailsViewModel(instance(), instance(), instance()) }
}