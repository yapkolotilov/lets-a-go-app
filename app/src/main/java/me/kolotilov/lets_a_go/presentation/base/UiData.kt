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
 * Данные для диалога.
 *
 * @param timeout Таймаут на просмотр. Если 0, то диалог автоматически не скрывается.
 * @param title Title.
 * @param message Message.
 * @param positiveButton PositiveButton.
 * @param negativeButton NegativeButton.
 * @param neutralButton NeutralButton.
 * @param cancelable Cancelable.
 * @param timeout Timeout.
 * @param onDismiss OnDismiss.
 */
data class DialogData(
    val title: String,
    val message: String? = null,
    val positiveButton: ButtonData,
    val negativeButton: ButtonData? = null,
    val neutralButton: ButtonData? = null,
    val cancelable: Boolean = true
)

/**
 * Показывает диалог.
 *
 * @param dialog Диалог.
 */
fun Fragment.showDialog(dialog: DialogData): AlertDialog {
    return AlertDialog.Builder(requireContext())
        .apply {
            setTitle(dialog.title)
            dialog.message?.let { setMessage(it) }
            dialog.positiveButton.let { setPositiveButton(it.text) { _, _ -> it.onClick() } }
            dialog.negativeButton?.let { setNegativeButton(it.text) { _, _ -> it.onClick() } }
            dialog.neutralButton?.let { setNegativeButton(it.text) { _, _ -> it.onClick() } }
            setCancelable(dialog.cancelable)
        }.show()
}