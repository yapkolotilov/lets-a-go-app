package me.kolotilov.lets_a_go.utils

import android.widget.EditText
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.joda.time.DateTime
import org.joda.time.Duration
import java.util.*

fun Date.toDateTime() = DateTime(this)

fun Duration.toDate() = Date(millis)

fun Date.toDuration() = Duration(time)

@Suppress("UNCHECKED_CAST")
fun <T> Any.castTo(): T {
    return this as T
}

fun <T> Subject<T>.emitNext(value: T?) {
    if (value != null)
        onNext(value)
}

fun <T : Any> BehaviorSubject<T>.value(): T {
    return value!!
}

fun EditText.smartSetText(text: CharSequence) {
    if (hasFocus())
        return
    setText(text)
}