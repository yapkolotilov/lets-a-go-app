package me.kolotilov.lets_a_go.ui.details.user

import android.view.View
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.ui.base.Recycler

class SymptomsFactory : Recycler.Factory<String> {

    override fun getType(item: String): Int {
        return R.layout.user_details_symptom_item
    }

    override fun getViewHolder(
        type: Int,
        itemView: View,
        delegate: Recycler.Delegate<String>
    ): Recycler.ViewHolder<String> {
        return SymptomViewHolder(itemView, delegate)
    }
}