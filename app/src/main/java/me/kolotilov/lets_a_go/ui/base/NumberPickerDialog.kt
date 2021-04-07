package me.kolotilov.lets_a_go.ui.base

import android.content.Context
import android.view.LayoutInflater
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.base.showCompat

/**
 * Диалог для выбора чисел.
 *
 * @param title Заголовок.
 * @param initialValue Изначальное значение.
 * @param listener Слушатель.
 */
class NumberPickerDialog(
    private val context: Context,
    private val title: String,
    private val valueRange: IntRange,
    private val initialValue: Int,
    private val listener: (Int) -> Unit
) {

    private var currentValue: Int = initialValue

    /**
     * Показывает диалог.
     *
     */
    fun show() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_number_picker, null, false)
        view.findViewById<NumberPicker>(R.id.number_picker).apply {
            minValue = valueRange.first
            maxValue = valueRange.last
            wrapSelectorWheel = false
            value = initialValue
            setOnValueChangedListener { _, _, newVal -> currentValue = newVal }
        }

        AlertDialog.Builder(context)
            .setTitle(title)
            .setView(view)
            .setPositiveButton(R.string.ok_button) { _, _ -> listener(currentValue) }
            .setNegativeButton(R.string.cancel_button) { _, _ -> }
            .create().showCompat()
    }
}