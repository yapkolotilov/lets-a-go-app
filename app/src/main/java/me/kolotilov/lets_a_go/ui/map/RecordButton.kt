package me.kolotilov.lets_a_go.ui.map

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import me.kolotilov.lets_a_go.R

class RecordButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.button_record, this)
    }
}