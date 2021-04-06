package me.kolotilov.lets_a_go.presentation.details

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.utils.emitNext
import ru.terrakok.cicerone.Router

class BasicInfoViewModel(
    private val userDetailsContainer: UserDetailsContainer,
    private val router: Router
) : BaseViewModel() {

    val name: Observable<String> get() = nameSubject
    private val nameSubject: BehaviorSubject<String> = BehaviorSubject.create()

    val age: Observable<String> get() = ageSubject.map { it.toString() }
    private val ageSubject: BehaviorSubject<Int> = BehaviorSubject.create()

    val height: Observable<String> get() = heightSubject.map { it.toString() }
    private val heightSubject: BehaviorSubject<Int> = BehaviorSubject.create()

    val weight: Observable<String> get() = weightSubject.map { it.toString() }
    private val weightSubject: BehaviorSubject<Int> = BehaviorSubject.create()

    val validation: Observable<Boolean> get() = validationSubject
    private val validationSubject: Subject<Boolean> = BehaviorSubject.create()

    init {
        userDetailsContainer.clear()
    }

    fun setName(name: String) {
        nameSubject.emitNext(name.takeIf { it.isNotEmpty() })
    }

    fun setAge(age: Int) {
        ageSubject.emitNext(age.takeIf { it > 0 })
        validate()
    }

    fun setHeight(height: Int) {
        heightSubject.emitNext(height.takeIf { it > 0 })
        validate()
    }

    fun setWeight(weight: Int) {
        weightSubject.emitNext(weight.takeIf { it > 0 })
        validate()
    }

    fun next() {
        if (validate()) {
            userDetailsContainer.apply {
                name = nameSubject.value
                age = ageSubject.value
                height = heightSubject.value
                weight = weightSubject.value
            }
        }
        router.navigateTo(Screens.chooseIllnesses())
    }

    fun skip() {
        router.navigateTo(Screens.userDetails())
    }

    private fun validate(): Boolean {
        val isValid = ageSubject.value ?: 0 > 0
                && heightSubject.value ?: 0 > 0
                && weightSubject.value ?: 0 > 0
        validationSubject.onNext(isValid)
        return isValid
    }
}