package me.kolotilov.lets_a_go.ui.details

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.ui.base.Grid
import me.kolotilov.lets_a_go.ui.base.InfoView
import me.kolotilov.lets_a_go.ui.base.KeyValueFactory
import me.kolotilov.lets_a_go.ui.base.toKeyValueModel
import me.kolotilov.lets_a_go.ui.distance
import me.kolotilov.lets_a_go.ui.duration
import me.kolotilov.lets_a_go.ui.icon
import me.kolotilov.lets_a_go.ui.name
import org.joda.time.Duration

class FilterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : InfoView<FilterView.Data>(context, attrs, R.layout.view_filter) {

    data class Data(
        val length: ClosedFloatingPointRange<Double>?,
        val duration: ClosedRange<Duration>?,
        val typesAllowed: List<Route.Type>?,
        val groundsAllowed: List<Route.Ground>?
    )

    override val cardView: View = findViewById(R.id.card_view)
    override val loadingLayout: View = findViewById(R.id.loading_layout)
    override val loadedLayout: View = findViewById(R.id.loaded_layout)
    override val emptyLayout: View = findViewById(R.id.empty_layout)
    private val filterGrid: GridLayout = findViewById(R.id.filter_grid)
    private val typesHintTextView: TextView = findViewById(R.id.types_hint_text_view)
    private val typesLayout: LinearLayout = findViewById(R.id.types_linear_layout)
    private val groundsHintTextView: TextView = findViewById(R.id.grounds_hint_text_view)
    private val groundsLayout: LinearLayout = findViewById(R.id.grounds_linear_layout)

    private val filterAdapter = Grid.ListAdapter(filterGrid, KeyValueFactory())

    override fun fillData(data: Data) {
        filterAdapter.items = listOf(
            Pair(
                context.getString(R.string.distance),
                data.length?.let {
                    context.getString(
                        R.string.from_to,
                        it.start.distance(context),
                        it.endInclusive.distance(context)
                    )
                }),
            Pair(
                context.getString(R.string.duration),
                data.duration?.let {
                    context.getString(
                        R.string.from_to,
                        it.start.duration(),
                        it.endInclusive.duration()
                    )
                }
            )
        ).filter { it.second != null }.map { it.toKeyValueModel() }

        val inflater = LayoutInflater.from(context)

        typesHintTextView.isVisible = !data.typesAllowed.isNullOrEmpty()
        typesLayout.isVisible = !data.typesAllowed.isNullOrEmpty()
        typesLayout.removeAllViews()
        for (type in data.typesAllowed ?: emptyList()) {
            val imageView = inflater.inflate(R.layout.route_type, typesLayout, false) as ImageView
            imageView.setImageResource(type.icon())
            typesLayout.addView(imageView)
        }

        groundsHintTextView.isVisible = !data.groundsAllowed.isNullOrEmpty()
        groundsHintTextView.isVisible = !data.groundsAllowed.isNullOrEmpty()
        groundsLayout.removeAllViews()
        for (ground in data.groundsAllowed ?: emptyList()) {
            val textView = inflater.inflate(R.layout.ground_type, groundsLayout, false) as TextView
            textView.text = ground.name(context)
            groundsLayout.addView(textView)
        }
    }

    fun setItems(
        length: ClosedFloatingPointRange<Double>?,
        duration: ClosedRange<Duration>?,
        typesAllowed: List<Route.Type>?,
        groundsAllowed: List<Route.Ground>?,
        enabled: Boolean
    ) {
        if (!enabled || listOf(length, duration, typesAllowed, groundsAllowed).all { it == null })
            setData(null)
        else
            setData(Data(length, duration, typesAllowed, groundsAllowed))
    }
}