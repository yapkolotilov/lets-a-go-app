package me.kolotilov.lets_a_go.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputLayout
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.models.Route
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormat
import java.text.DecimalFormat
import kotlin.math.roundToInt

//region Map API

/**
 * Прерващает точку в координаты [LatLng].
 */
fun Point.toLatLng() = LatLng(latitude, longitude)

fun Location.toPoint() = Point(latitude, longitude, altitude, DateTime(time))

fun Location.toLatLng() = LatLng(latitude, longitude)

//endregion

//region Android

/**
 * Возвращает текст внутри поля.
 */
var TextInputLayout.text: String
    get() = editText?.text?.toString() ?: ""
    set(value) {
        editText?.setText(value)
    }


/**
 * [doAfterTextChanged].
 *
 * @param callback Колбэк.
 */
fun TextInputLayout.doAfterTextChanged(callback: (String) -> Unit) {
    editText?.doAfterTextChanged { callback(it.toString()) }
}

@SuppressLint("ClickableViewAccessibility")
fun TextInputLayout.setTouchListener(listener: () -> Unit) {
    editText?.setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            listener()
            true
        } else
            true
    }
}

/**
 * Шорткат для сборки фрагмента.
 *
 * @param argumentsBuilder Сборщик аргументов.
 */
inline fun <T : Fragment> T.buildArguments(argumentsBuilder: Bundle.() -> Unit): T {
    arguments = Bundle().also(argumentsBuilder)
    return this
}

/**
 * Шорткат контекста.
 */
val RecyclerView.ViewHolder.context: Context get() = itemView.context

/**
 * Переводит число в dp.
 */
fun Number.dp(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).roundToInt()
}

/**
 * Переводит число в sp.
 */
fun Number.sp(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        context.resources.displayMetrics
    ).roundToInt()
}

@ColorInt
fun Context.getColorCompat(@ColorRes color: Int): Int {
    return ContextCompat.getColor(this, color)
}

fun View.getString(@StringRes stringRes: Int): String {
    return context.getString(stringRes)
}

//endregion

//region Модели

private val singleDigitFormatter = DecimalFormat().apply { maximumFractionDigits = 1 }

fun Double.distance(context: Context): String {
    val name: String
    val value: Double

    if (this >= 1000) {
        value = this / 1000.0
        name = context.getString(R.string.km)
    } else {
        value = this
        name = context.getString(R.string.m)
    }
    return "${singleDigitFormatter.format(value)} $name"
}

fun Duration.duration(context: Context): String {
    return DateTimeFormat.forPattern("HH:mm").print(DateTime(millis))
}

fun Double.speed(context: Context): String {
    return context.getString(R.string.speed, singleDigitFormatter.format(this))
}

fun Int.kilocalories(context: Context): String {
    return context.getString(R.string.kilocalories, this)
}

@DrawableRes
fun Route.Type.icon(): Int {
    return when (this) {
        Route.Type.WALKING -> R.drawable.ic_type_walking
        Route.Type.RUNNING -> R.drawable.ic_type_running
        Route.Type.CYCLING -> R.drawable.ic_type_cycling
    }
}

fun Route.Ground.name(context: Context): String {
    return when (this) {
        Route.Ground.ASPHALT -> context.getString(R.string.ground_asphalt)
        Route.Ground.TRACK -> context.getString(R.string.ground_track)
    }
}

//endregion