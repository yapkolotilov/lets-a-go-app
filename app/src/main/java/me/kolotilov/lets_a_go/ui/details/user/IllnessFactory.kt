package me.kolotilov.lets_a_go.ui.details.user

import android.view.View
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.ui.base.Recycler

class IllnessFactory : Recycler.Factory<String> {

    override fun getType(item: String): Int {
        return R.layout.user_details_illness_item
    }

    override fun getViewHolder(
        type: Int,
        view: View,
        delegate: Recycler.Delegate<String>
    ): Recycler.ViewHolder<String> {
        return IllnessViewHolder(view, delegate)
    }
}