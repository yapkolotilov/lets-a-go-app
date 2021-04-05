package me.kolotilov.lets_a_go.presentation.auth

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.network.NetworkRepository
import me.kolotilov.lets_a_go.network.ServerException
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Screens
import ru.terrakok.cicerone.Router

class LoginViewModel(
    private val networkRepository: NetworkRepository,
    private val router: Router
) : BaseViewModel() {

    val loginEnabled: Observable<Boolean> get() = loginEnabledSubject
    private val loginEnabledSubject: Subject<Boolean> = BehaviorSubject.create()

    val errorDialog: Observable<String> get() = errorDialogSubject
    private val errorDialogSubject: Subject<String> = BehaviorSubject.create()

    fun updateLoginButton(email: String, password: String) {
        val enabled = email.isNotEmpty() && password.isNotEmpty()
        loginEnabledSubject.onNext(enabled)
    }

    fun login(email: String, password: String) {
        networkRepository.login(email, password)
            .load()
            .doOnComplete {
                router.newRootScreen(Screens.MapScreen)
            }
            .doOnError {
                if (it is ServerException)
                    errorDialogSubject.onNext(it.message.toString())
            }
            .emptySubscribe()
            .autoDispose()
    }

    fun register() {
        router.navigateTo(Screens.RegisterScreen)
    }
}