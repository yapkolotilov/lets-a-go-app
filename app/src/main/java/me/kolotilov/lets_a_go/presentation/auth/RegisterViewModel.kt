package me.kolotilov.lets_a_go.presentation.auth

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.network.NetworkRepository
import me.kolotilov.lets_a_go.network.ServerException
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Screens
import ru.terrakok.cicerone.Router

class RegisterViewModel(
    private val networkRepository: NetworkRepository,
    private val router: Router
) : BaseViewModel() {

    val validation: Observable<Boolean> get() = validationSubject
    private val validationSubject: Subject<Boolean> = BehaviorSubject.create()

    fun register(email: String, password: String) {
        networkRepository.register(email, password)
            .andThen(networkRepository.login(email, password))
            .load()
            .doOnComplete {
                router.newRootScreen(Screens.BasicInfoScreen)
            }
            .doOnError {
                if (it is ServerException)
                    showPopup(it.message)
            }
            .emptySubscribe()
            .autoDispose()
    }

    fun updateRegisterButton(email: String, password: String, repeatPassword: String) {
        val isValid = email.isNotEmpty() && password.isNotEmpty() && password == repeatPassword
        validationSubject.onNext(isValid)
    }
}