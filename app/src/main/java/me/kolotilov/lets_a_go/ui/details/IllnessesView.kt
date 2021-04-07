package me.kolotilov.lets_a_go.ui.details

import android.content.Context
import android.util.AttributeSet
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Illness

/**
 * Список болезней.
 */
class IllnessesView constructor(
    context: Context,
    attrs: AttributeSet? = null
) : DetailsListView<Illness>(context, attrs) {

    override val title: String get() = context.getString(R.string.illnesses_title)
    override val placeholderText: String get() = context.getString(R.string.illnesses_placeholder)

    override fun getName(item: Illness) = item.name
}