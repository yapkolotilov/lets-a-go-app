package me.kolotilov.lets_a_go.presentation.base

import android.app.Dialog
import android.widget.Button
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import me.kolotilov.lets_a_go.ui.dp

/**
 * Данные для кнопки
 *
 * @param text Текст кнопки.
 * @param color Цвет кнопки.
 * @param onClick Колбэк по нажатию на кнопку.
 */
open class ButtonData(
    val text: String,
    @ColorInt
    val color: Int? = null,
    open val onClick: () -> Unit = {}
)

/**
 * Показывает диалог.
 *
 * @param dialog Диалог.
 */
fun Fragment.showDialog(
    title: String,
    message: String? = null,
    positiveButton: ButtonData,
    negativeButton: ButtonData? = null,
    neutralButton: ButtonData? = null,
    cancelable: Boolean = false
) {
    AlertDialog.Builder(requireContext())
        .apply {
            setTitle(title)
            message?.let { setMessage(it) }
            positiveButton.let { setPositiveButton(it.text) { _, _ -> it.onClick() } }
            negativeButton?.let { setNegativeButton(it.text) { _, _ -> it.onClick() } }
            neutralButton?.let { setNegativeButton(it.text) { _, _ -> it.onClick() } }
            setCancelable(cancelable)
        }.create().showCompat()
}

fun AlertDialog.showCompat() {
    setOnShowListener {
        getButton(AlertDialog.BUTTON_POSITIVE)?.let { designButton(it) }
        getButton(AlertDialog.BUTTON_NEGATIVE)?.let { designButton(it) }
        getButton(AlertDialog.BUTTON_NEUTRAL)?.let { designButton(it) }
    }
    show()
}

fun android.app.AlertDialog.showCompat() {
    setOnShowListener {
        getButton(AlertDialog.BUTTON_POSITIVE)?.let { designButton(it) }
        getButton(AlertDialog.BUTTON_NEGATIVE)?.let { designButton(it) }
        getButton(AlertDialog.BUTTON_NEUTRAL)?.let { designButton(it) }
    }
    show()
}

private fun Dialog.designButton(button: Button) {
    val horizontalPadding = 24.dp(context)
    val verticalPadding = 20.dp(context)
    button.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
}
