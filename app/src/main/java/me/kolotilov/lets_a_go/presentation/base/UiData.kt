package me.kolotilov.lets_a_go.presentation.base

import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

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
    cancelable: Boolean = true
): AlertDialog {
    return AlertDialog.Builder(requireContext())
        .apply {
            setTitle(title)
            message?.let { setMessage(it) }
            positiveButton.let { setPositiveButton(it.text) { _, _ -> it.onClick() } }
            negativeButton?.let { setNegativeButton(it.text) { _, _ -> it.onClick() } }
            neutralButton?.let { setNegativeButton(it.text) { _, _ -> it.onClick() } }
            setCancelable(cancelable)
        }.show()
}

