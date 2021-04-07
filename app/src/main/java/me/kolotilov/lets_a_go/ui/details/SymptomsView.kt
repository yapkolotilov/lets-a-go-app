package me.kolotilov.lets_a_go.ui.details

import android.content.Context
import android.util.AttributeSet
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Symptom

/**
 * Список симптомов.
 */
class SymptomsView constructor(
    context: Context,
    attrs: AttributeSet? = null
) : DetailsListView<Symptom>(context, attrs) {

    override val title: String get() = context.getString(R.string.symptoms_title)
    override val placeholderText: String get() = context.getString(R.string.symptoms_placeholder)

    override fun getName(item: Symptom) = item.name
}