package me.kolotilov.lets_a_go.presentation.details

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.models.*
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.ui.details.EditDetailsType
import org.joda.time.DateTime
import ru.terrakok.cicerone.Router

/**
 * Логика личного кабинета.
 */
class UserDetailsViewModel(
    private val repository: Repository,
    private val router: Router
) : BaseViewModel() {

    data class Data(
        val username: String,
        val name: String?,
        val age: Int?,
        val birthDate: DateTime?,
        val height: Int?,
        val weight: Int?,
        val illnesses: List<Illness>,
        val symptoms: List<Symptom>,
        val filter: Filter,
        val totalDistance: Double,
        val totalKilocaloriesBurnt: Int?,
        val routes: List<RouteItem>,
        val entries: List<RouteEntry>
    )

    /**
     * Информация о пользователе.
     */
    val userDetails: Observable<Data> get() = userDetailsSubject
    private val userDetailsSubject: Subject<Data> = BehaviorSubject.create()


    override fun attach() {
        repository.getDetails(repository.lastLocation)
            .load()
            .doOnSuccess {
                val data = Data(
                    username = it.username,
                    name = it.name,
                    age = it.age,
                    birthDate = it.birthDate,
                    height = it.height,
                    weight = it.weight,
                    illnesses = it.illnesses,
                    symptoms = it.symptoms,
                    filter = it.filter,
                    totalDistance = it.totalDistance,
                    totalKilocaloriesBurnt = it.totalKilocaloriesBurnt,
                    routes = it.routes,
                    entries = it.entries
                )
                userDetailsSubject.onNext(data)
            }
            .emptySubscribe()
            .autoDispose()
    }

    /**
     * Изменение информации о пользователе.
     */
    fun editBasicInfo() {
        router.navigateTo(Screens.basicInfo(EditDetailsType.USER_DETAILS))
    }

    /**
     * Изменение информации о болезнях.
     */
    fun editIllnesses() {
        router.navigateTo(Screens.chooseIllnesses(EditDetailsType.USER_DETAILS))
    }

    /**
     * Изменение информации о жалобах.
     */
    fun editSymptoms() {
        router.navigateTo(Screens.chooseSymptoms(EditDetailsType.USER_DETAILS))
    }

    fun editFilter() {
        router.navigateTo(Screens.editFilter(EditDetailsType.USER_DETAILS))
    }

    /**
     * Разлогиниться.
     */
    fun logOut() {
        repository.token = ""
        router.newRootScreen(Screens.login())
    }
}