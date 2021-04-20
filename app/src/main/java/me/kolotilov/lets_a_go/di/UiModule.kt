package me.kolotilov.lets_a_go.di

import me.kolotilov.lets_a_go.App
import me.kolotilov.lets_a_go.presentation.EmptyViewModel
import me.kolotilov.lets_a_go.presentation.SearchRoutesViewModel
import me.kolotilov.lets_a_go.presentation.Tags
import me.kolotilov.lets_a_go.presentation.auth.EmailViewModel
import me.kolotilov.lets_a_go.presentation.auth.LoginViewModel
import me.kolotilov.lets_a_go.presentation.auth.RegisterViewModel
import me.kolotilov.lets_a_go.presentation.details.*
import me.kolotilov.lets_a_go.presentation.details.onboarding.OnboardingEndViewModel
import me.kolotilov.lets_a_go.presentation.details.onboarding.OnboardingTitleViewModel
import me.kolotilov.lets_a_go.presentation.details.onboarding.PermissionViewModel
import me.kolotilov.lets_a_go.presentation.map.*
import me.kolotilov.lets_a_go.ui.map.LocationService
import me.kolotilov.lets_a_go.ui.map.MapServiceViewModel
import me.kolotilov.lets_a_go.ui.map.getLocationService
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
    bind<DateTimeFormatter>(Tags.RECORDING_TIME) with provider { recordTimeFormatter() }
    bind<DateTimeFormatter>(Tags.ENTRY_DATE) with provider { entryDateFormatter() }
    bind<DecimalFormat>(Tags.DISTANCE) with provider { distanceFormatter() }
//    bind<LocationService>() with provider { DebugLocationServiceImpl(instance()) }
//    bind<LocationService>() with provider { ReleaseLocationServiceImpl(instance()) }
    bind<LocationService>() with provider { getLocationService(instance()) }

    bind<EmptyViewModel>() with provider { EmptyViewModel() }
    bind<LoginViewModel>() with provider { LoginViewModel(instance(), instance(), instance()) }
    bind<EditBasicInfoViewModel>() with provider { EditBasicInfoViewModel(instance(), instance()) }
    bind<UserDetailsViewModel>() with provider { UserDetailsViewModel(instance(), instance()) }
    bind<RegisterViewModel>() with provider { RegisterViewModel(instance(), instance()) }
    bind<ChooseIllnessesViewModel>() with provider {
        ChooseIllnessesViewModel(instance(), instance())
    }
    bind<ChooseSymptomsViewModel>() with provider {
        ChooseSymptomsViewModel(instance(), instance(), instance())
    }
    bind<OnboardingTitleViewModel>() with provider { OnboardingTitleViewModel(instance()) }
    bind<MapViewModel>() with provider { MapViewModel(instance(), instance(), instance()) }
    bind<EditRouteViewModel>() with provider { EditRouteViewModel(instance(), instance()) }
    bind<EntryPreviewViewModel>() with provider { EntryPreviewViewModel(instance(), instance()) }
    bind<EntryDetailsViewModel>() with provider { EntryDetailsViewModel(instance()) }
    bind<RouteDetailsViewModel>() with provider { RouteDetailsViewModel(instance(), instance()) }
    bind<EditFilterViewModel>() with provider { EditFilterViewModel(instance(), instance()) }
    bind<OnboardingEndViewModel>() with provider { OnboardingEndViewModel(instance()) }
    bind<SearchRoutesViewModel>() with provider { SearchRoutesViewModel(instance(), instance()) }
    bind<MapServiceViewModel>() with provider { MapServiceViewModel(instance()) }
    bind<PermissionViewModel>() with provider { PermissionViewModel(instance()) }
    bind<EmailViewModel>() with provider { EmailViewModel(instance(), instance()) }
}

private fun recordTimeFormatter(): DateTimeFormatter {
    return DateTimeFormat.forPattern("HH:mm:ss")
}

private fun birthDateFormatter(): DateTimeFormatter {
    return DateTimeFormat.forPattern("d MMMM yyyy")
}

private fun entryDateFormatter(): DateTimeFormatter {
    return DateTimeFormat.forPattern("dd MMMM HH:mm")
}

private fun distanceFormatter(): DecimalFormat {
    return DecimalFormat().apply { maximumFractionDigits = 1 }
}
