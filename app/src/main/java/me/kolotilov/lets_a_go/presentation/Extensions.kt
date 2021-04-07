package me.kolotilov.lets_a_go.utils

import android.widget.EditText
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.joda.time.DateTime
import org.joda.time.Duration
import ru.terrakok.cicerone.Router
import java.util.*

//region Kotlin

/**
 * Приводит объект к типу T.
 */
@Suppress("UNCHECKED_CAST")
fun <T> Any.castTo(): T {
    return this as T
}

/**
 * Приводит объект к типу T.
 */
@Suppress("UNCHECKED_CAST")
fun <T> Any.castToOrNull(): T? {
    return this as? T
}

//endregion

//region RxJava

/**
 * Пропускает null-значения.
 *
 * @param value Значение.
 */
fun <T> Subject<T>.emitNext(value: T?) {
    if (value != null)
        onNext(value)
}

/**
 * Возвращает not-null значение.
 *
 */
fun <T : Any> BehaviorSubject<T>.value(): T {
    return value!!
}

/**
 * Устанавливает значение.
 *
 * @param text Текст.
 */
fun EditText.smartSetText(text: CharSequence) {
    if (hasFocus())
        return
    setText(text)
}

//endregion

//region JodaTime

/**
 * Переводит в тип [DateTime].
 */
fun Date.toDateTime() = DateTime(this)

/**
 * Переводит в тип [Date].
 */
fun Duration.toDate() = Date(millis)

/**
 * Переводит в [Duration].
 */
fun Date.toDuration() = Duration(time)

//endregion

//region Cicerone

/**
 * Применение нескольких операторов к роутеру.
 *
 * @param body Тело.
 */
inline operator fun Router.invoke(body: Router.() -> Unit) {
    this.body()
}

//endregion