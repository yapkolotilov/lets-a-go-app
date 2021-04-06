package me.kolotilov.lets_a_go.presentation.auth

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.models.ErrorCode
import me.kolotilov.lets_a_go.models.ServiceException
import me.kolotilov.lets_a_go.network.NetworkRepository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Screens
import ru.terrakok.cicerone.Router

/**
 * Логика экрана регистрации.
 */
class RegisterViewModel(
    private val networkRepository: NetworkRepository,
    private val router: Router
) : BaseViewModel() {

    /**
     * Если придёт true, то кнопка регистрации станет активной.
     */
    val validation: Observable<Boolean> get() = validationSubject
    private val validationSubject: Subject<Boolean> = BehaviorSubject.create()

    val error: Observable<ErrorCode> get() = errorSubject
    private val errorSubject: Subject<ErrorCode> = PublishSubject.create()

    /**
     * Регистрация.
     *
     * @param email email.
     * @param password Пароль.
     */
    fun register(email: String, password: String) {
        networkRepository.register(email, password)
            .andThen(networkRepository.login(email, password))
            .load()
            .doOnComplete {
                router.newRootScreen(Screens.basicInfo())
            }
            .doOnError {
                if (it is ServiceException)
                    errorSubject.onNext(it.code)
            }
            .emptySubscribe()
            .autoDispose()
    }

    /**
     * Валидирует форму.
     *
     * @param email email.
     * @param password Пароль.
     * @param repeatPassword Повторный пароль.
     */
    fun updateRegisterButton(email: String, password: String, repeatPassword: String) {
        val isValid =
            email.isNotEmpty() && password.isNotEmpty() && password == repeatPassword
        validationSubject.onNext(isValid)
    }
}