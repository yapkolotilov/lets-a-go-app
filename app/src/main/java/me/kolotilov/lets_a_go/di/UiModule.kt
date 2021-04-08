package me.kolotilov.lets_a_go.di

import me.kolotilov.lets_a_go.App
import me.kolotilov.lets_a_go.presentation.EmptyViewModel
import me.kolotilov.lets_a_go.presentation.Tags
import me.kolotilov.lets_a_go.presentation.auth.LoginViewModel
import me.kolotilov.lets_a_go.presentation.auth.RegisterViewModel
import me.kolotilov.lets_a_go.presentation.details.*
import me.kolotilov.lets_a_go.presentation.details.onboarding.OnboardingEndViewModel
import me.kolotilov.lets_a_go.presentation.details.onboarding.OnboardingTitleViewModel
import me.kolotilov.lets_a_go.presentation.map.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import ru.terrakok.cicerone.NavigatorHolder
import java.text.DecimalFormat

fun uiModule() = DI.Module("App") {
    bind<NavigatorHolder>() with provider { instance<App>().getNavigatorHolder() }
    bind<DateTimeFormatter>(Tags.BIRTH_DATE) with provider { birthDateFormatter() }
    bind<DecimalFormat>(Tags.DISTANCE) with provider { distanceFormatter() }

    bind<EmptyViewModel>() with provider { EmptyViewModel() }
    bind<LoginViewModel>() with provider { LoginViewModel(instance(), instance()) }
    bind<EditBasicInfoViewModel>() with provider { EditBasicInfoViewModel(instance(), instance()) }
    bind<UserDetailsViewModel>() with provider { UserDetailsViewModel(instance(), instance()) }
    bind<RegisterViewModel>() with provider { RegisterViewModel(instance(), instance()) }
    bind<ChooseIllnessesViewModel>() with provider {
        ChooseIllnessesViewModel(instance(), instance())
    }
    bind<ChooseSymptomsViewModel>() with provider {
        ChooseSymptomsViewModel(instance(), instance())
    }
    bind<OnboardingTitleViewModel>() with provider { OnboardingTitleViewModel(instance()) }
    bind<MapViewModel>() with provider { MapViewModel(instance(), instance(), instance()) }
    bind<EditRouteViewModel>() with provider { EditRouteViewModel(instance(), instance(), instance()) }
    bind<EditEntryViewModel>() with provider { EditEntryViewModel(instance(), instance(), instance()) }
    bind<EntryDetailsViewModel>() with provider { EntryDetailsViewModel(instance()) }
    bind<RouteDetailsViewModel>() with provider { RouteDetailsViewModel(instance(), instance(), instance()) }
    bind<EditFilterViewModel>() with provider { EditFilterViewModel(instance(), instance()) }
    bind<OnboardingEndViewModel>() with provider { OnboardingEndViewModel(instance()) }
}

private fun birthDateFormatter(): DateTimeFormatter {
    return DateTimeFormat.forPattern("d MMMM yyyy")
}

private fun distanceFormatter(): DecimalFormat {
    return DecimalFormat().apply { maximumFractionDigits = 1 }
}