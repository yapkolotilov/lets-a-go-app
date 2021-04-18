package me.kolotilov.lets_a_go.ui.map

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import me.kolotilov.lets_a_go.R

class RecordButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.button_record, this)
    }

    private val recordImage: View = findViewById(R.id.record_image_view)

    private var isRecording: Boolean = false
    var allowClicks: Boolean = true

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener {
            if (allowClicks) {
                l?.onClick(it)
            }
        }
    }

    fun setRecording(isRecording: Boolean) {
        this.isRecording = isRecording
        val scale = if (isRecording) 1.8f else 1f
        recordImage.animate()
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(500)
            .start()
    }
}