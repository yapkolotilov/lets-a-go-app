package me.kolotilov.lets_a_go.ui

import android.location.Location
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputLayout
import me.kolotilov.lets_a_go.models.Point
import org.joda.time.DateTime

//region Map API

/**
 * Прерващает точку в координаты [LatLng].
 */
fun Point.toLatLng() = LatLng(latitude, longitude)

fun LatLng.toPoint() = Point(latitude, longitude, DateTime.now(), -1)

fun Location.toPoint() = Point(latitude, longitude, DateTime(time), -1)

fun Location.toLatLng() = LatLng(latitude, longitude)

//endregion

//region TextInputLayout

/**
 * Возвращает текст внутри поля.
 */
val TextInputLayout.text: String get() = editText?.text?.toString() ?: ""

fun TextInputLayout.doAfterTextChanged(callback: (String) -> Unit) {
    editText?.doAfterTextChanged { callback(it.toString()) }
}

//endregion

val RecyclerView.ViewHolder.context get() = itemView.context