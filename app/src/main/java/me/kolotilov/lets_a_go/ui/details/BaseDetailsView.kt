package me.kolotilov.lets_a_go.ui.details

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.view.isVisible
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.ui.base.Grid
import me.kolotilov.lets_a_go.ui.base.InfoView
import me.kolotilov.lets_a_go.ui.base.KeyValueFactory
import me.kolotilov.lets_a_go.ui.base.toKeyValueModel

/**
 * Базовая информация о здоровье.
 */
class BaseDetailsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : InfoView<BaseDetailsView.Data>(context, attrs, R.layout.view_base_info) {

    data class Data(
        val name: String?,
        val age: Int?,
        val height: Int?,
        val weight: Int?
    )

    override val cardView: View = findViewById(R.id.card_view)
    override val loadingLayout: View = findViewById(R.id.loading_layout)
    override val loadedLayout: View = findViewById(R.id.loaded_layout)
    override val emptyLayout: View = findViewById(R.id.empty_layout)

    private val nameTextView: TextView = findViewById(R.id.name_text_view)
    private val detailsGrid: GridLayout = findViewById(R.id.details_grid)
    private val detailsAdapter: Grid.ListAdapter

    init {
        detailsAdapter = Grid.ListAdapter(detailsGrid, KeyValueFactory())
    }

    fun setData(name: String?, age: Int?, height: Int?, weight: Int?) {
        val data = if (listOf(name, age, height, weight).all { it == null })
            null
        else
            Data(
                name = name,
                age = age,
                height = height,
                weight = weight
            )
        setData(data)
    }

    override fun fillData(data: Data) {
        nameTextView.text = data.name
        nameTextView.isVisible = data.name != null

        detailsAdapter.items = listOf(
            Pair(
                context.getString(R.string.age_key),
                data.age?.let { "$it ${context.resources.getQuantityString(R.plurals.age, it)}" }
            ),
            Pair(
                context.getString(R.string.height_key),
                data.height?.let { "$it ${context.getString(R.string.cm)}" }
            ),
            Pair(
                context.getString(R.string.weight_key),
                data.weight?.let { "$it ${context.getString(R.string.kg)}" }
            )
        ).filter { it.second != null }.map { it.toKeyValueModel() }
    }
}