package me.kolotilov.lets_a_go.ui.details

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.ui.base.InfoView

@Suppress("LeakingThis")
abstract class DetailsListView<T>(
    context: Context,
    attrs: AttributeSet?
) : InfoView<DetailsListView.Data<T>>(context, attrs, R.layout.view_items) {

    data class Data<T>(
        val items: List<T>
    )

    protected abstract val title: String
    protected abstract val placeholderText: String

    override val cardView: View = findViewById(R.id.card_view)
    override val loadingLayout: View = findViewById(R.id.loading_layout)
    override val loadedLayout: View = findViewById(R.id.loaded_layout)
    override val emptyLayout: View = findViewById(R.id.empty_layout)
    private val titleTextView: TextView = findViewById(R.id.title_text_view)
    private val placeholderTextView: TextView = findViewById(R.id.placeholder_text_view)
    private val illnessesLayout: LinearLayout = findViewById(R.id.illnesses_recycler)

    init {
        placeholderTextView.text = placeholderText
        titleTextView.text = title
    }

    override fun fillData(data: Data<T>) {
        val inflater = LayoutInflater.from(context)
        illnessesLayout.removeAllViews()
        for (i in data.items.indices) {
            val textView = inflater.inflate(R.layout.details_item, illnessesLayout, false) as TextView
            textView.text = getName(data.items[i])
            illnessesLayout.addView(textView)
        }
    }

    /**
     * Устанавливает значение.
     *
     * @param items Список.
     */
    fun setItems(items: List<T>) {
        setData(if (items.isNotEmpty()) Data(items) else null)
    }

    protected abstract fun getName(item: T): String
}