package me.kolotilov.lets_a_go.ui.details.user

import android.view.View
import android.widget.TextView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.ui.base.Grid
import me.kolotilov.lets_a_go.ui.base.Recycler

class IllnessViewHolder(itemView: View, delegate: Recycler.Delegate<String>) :
    Recycler.ViewHolder<String>(itemView, delegate) {

    private val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)

    override fun bind(item: String, selected: Boolean) {
        nameTextView.text = item
    }
}

class SymptomViewHolder(itemView: View, delegate: Recycler.Delegate<String>) :
    Recycler.ViewHolder<String>(itemView, delegate) {

    private val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)

    override fun bind(item: String, selected: Boolean) {
        nameTextView.text = item
    }
}

class BaseDetailsViewHolder(
    private val keyView: TextView,
    private val valueView: TextView
) : Grid.ViewHolder<BaseDetailsViewModel>(listOf(keyView, valueView)) {

    override fun bind(element: BaseDetailsViewModel) {
        keyView.text = element.key
        valueView.text = element.value
    }
}