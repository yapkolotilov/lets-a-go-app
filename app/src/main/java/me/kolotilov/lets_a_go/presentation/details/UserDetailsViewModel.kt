package me.kolotilov.lets_a_go.presentation.details

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.models.UserDetails
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.ui.details.BaseChooseFragment
import ru.terrakok.cicerone.Router

/**
 * Логика личного кабинета.
 */
class UserDetailsViewModel(
    private val repository: Repository,
    private val router: Router
) : BaseViewModel() {

    /**
     * Информация о пользователе.
     */
    val userDetails: Observable<UserDetails> get() = userDetailsSubject
    private val userDetailsSubject: Subject<UserDetails> = BehaviorSubject.create()

    private var userDetailsCache: UserDetails? = null

    override fun attach() {
        repository.getDetails()
            .load()
            .doOnSuccess {
                userDetailsCache = it
                userDetailsSubject.onNext(it)
            }
            .emptySubscribe()
            .autoDispose()
    }

    /**
     * Изменение информации о пользователе.
     */
    fun editBasicInfo() {
        val userDetailsCache = userDetailsCache ?: return
        router.navigateTo(
            Screens.basicInfo(
                name = userDetailsCache.name,
                birthDate = userDetailsCache.birthDate,
                height = userDetailsCache.height,
                weight = userDetailsCache.weight
            )
        )
    }

    /**
     * Изменение информации о болезнях.
     */
    fun editIllnesses() {
        router.navigateTo(
            Screens.chooseIllnesses(
                BaseChooseFragment.Type.USER_DETAILS,
                userDetailsCache?.illnesses ?: emptyList()
            )
        )
    }

    /**
     * Изменение информации о жалобах.
     */
    fun editSymptoms() {
        router.navigateTo(
            Screens.chooseSymptoms(
                BaseChooseFragment.Type.USER_DETAILS,
                userDetailsCache?.symptoms ?: emptyList()
            )
        )
    }

    fun editFilter() {
        router.navigateTo(Screens.editFilter())
    }

    /**
     * Разлогиниться.
     */
    fun logOut() {
        repository.token = ""
        router.newRootScreen(Screens.login())
    }
}