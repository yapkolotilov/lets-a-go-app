package me.kolotilov.lets_a_go.presentation.details

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.models.UserDetails
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Screens
import ru.terrakok.cicerone.Router

class UserDetailsViewModel(
    private val repository: Repository,
    private val router: Router
) : BaseViewModel() {

    val userDetails: Observable<UserDetails> get() = userDetailsSubject
    private val userDetailsSubject: Subject<UserDetails> = BehaviorSubject.create()

    override fun attach() {
        repository.getDetails()
            .load()
            .doOnSuccess {
                userDetailsSubject.onNext(it)
            }
            .emptySubscribe()
            .autoDispose()
    }

    fun logOut() {
        repository.token = ""
        router.newRootScreen(Screens.LoginScreen)
    }
}