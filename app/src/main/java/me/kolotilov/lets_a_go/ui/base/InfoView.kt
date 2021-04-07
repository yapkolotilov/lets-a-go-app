package me.kolotilov.lets_a_go.ui.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible

/**
 * Базовая информация.
 */
abstract class InfoView<D : Any>(
    context: Context,
    attrs: AttributeSet?,
    @LayoutRes
    layoutRes: Int
) : FrameLayout(context, attrs) {

    private enum class State {

        LOADING,

        LOADED,

        EMPTY
    }

    protected abstract val cardView: View
    protected abstract val loadingLayout: View
    protected abstract val loadedLayout: View
    protected abstract val emptyLayout: View

    init {
        inflate(context, layoutRes, this)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        cardView.setOnClickListener(l)
    }

    /**
     * Устанавливает контент.
     *
     * @param data Контент.
     */
    protected fun setData(data: D?) {
        loadingLayout.isVisible = false
        loadedLayout.isVisible = false
        emptyLayout.isVisible = false
        if (data != null) {
            loadedLayout.isVisible = true
            fillData(data)
        } else {
            emptyLayout.isVisible = true
        }
    }

    protected abstract fun fillData(data: D)
}