package me.kolotilov.lets_a_go.presentation.auth

import android.util.Log
import io.reactivex.Observable
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Screens
import ru.terrakok.cicerone.Router
import java.util.concurrent.TimeUnit

class EmailViewModel(
    private val router: Router,
    private val repository: Repository
) : BaseViewModel() {

    private var email: String = ""
    private var password: String = ""

    fun init(email: String, password: String) {
        this.email = email
        this.password = password
    }

    override fun attach() {
        Observable.interval(5, TimeUnit.SECONDS)
            .flatMap { repository.login(email, password).andThen(Observable.just(Unit)) }
            .load()
            .retry()
            .doOnNext {
                Log.d("BRUH", "success")
                router.newRootScreen(Screens.onboarding())
            }
            .emptySubscribe()
            .autoDispose()
    }
}