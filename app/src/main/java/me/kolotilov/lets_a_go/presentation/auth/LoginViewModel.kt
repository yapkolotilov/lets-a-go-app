package me.kolotilov.lets_a_go.presentation.auth

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.models.ErrorCode
import me.kolotilov.lets_a_go.models.ServiceException
import me.kolotilov.lets_a_go.network.NetworkRepository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Constants
import me.kolotilov.lets_a_go.presentation.Screens
import ru.terrakok.cicerone.Router

/**
 * Вьюмодель для экрана логина.
 */
class LoginViewModel(
    private val networkRepository: NetworkRepository,
    private val router: Router
) : BaseViewModel() {

    /**
     * Если пришло true, то кнопка входа активна.
     */
    val loginEnabled: Observable<Boolean> get() = loginEnabledSubject
    private val loginEnabledSubject: Subject<Boolean> = BehaviorSubject.create()

    /**
     * Присылает коды ошибок входа.
     */
    val errorDialog: Observable<ErrorCode> get() = errorDialogSubject
    private val errorDialogSubject: Subject<ErrorCode> = BehaviorSubject.create()

    /**
     * Запрос обновлений логина.
     *
     * @param email email.
     * @param password Пароль.
     */
    fun updateLoginButton(email: String, password: String) {
        val enabled = email.isNotEmpty() && password.length >= Constants.MIN_PASSWORD_LENGTH
        loginEnabledSubject.onNext(enabled)
    }

    /**
     * Вход в приложение.
     *
     * @param email email.
     * @param password Пароль.
     */
    fun login(email: String, password: String) {
        networkRepository.login(email, password)
            .load()
            .doOnComplete {
                router.newRootScreen(Screens.map())
            }
            .doOnError {
                if (it is ServiceException)
                    errorDialogSubject.onNext(it.code)
            }
            .emptySubscribe()
            .autoDispose()
    }

    /**
     * Переход на страницу регистрации.
     */
    fun register() {
        router.navigateTo(Screens.register())
    }
}