package me.kolotilov.lets_a_go.ui.map

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.ui.dp
import me.kolotilov.lets_a_go.utils.castTo

class RecordButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.button_record, this)
    }

    private val recordImage: View = findViewById(R.id.record_image_view)
    private val outlineView: ConstraintLayout = findViewById(R.id.outline_view)

    private var isRecording: Boolean = false

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener {
            if (isClickable) {
                l?.onClick(it)
                setRecording(!isRecording)
            }
        }
    }

    fun setRecording(isRecording: Boolean) {
        this.isRecording = isRecording
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 500
            addUpdateListener {
                val deltaTime = if (!isRecording) it.animatedFraction else 1 - it.animatedFraction
                recordImage.layoutParams.castTo<ConstraintLayout.LayoutParams>().apply {
                    val newMargin = (4.dp(context) + deltaTime * 12.dp(context)).toInt()
                    setMargins(newMargin, newMargin, newMargin, newMargin)
                }
                recordImage.requestLayout()
            }
        }.start()
    }
}